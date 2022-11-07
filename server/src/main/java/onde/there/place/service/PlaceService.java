package onde.there.place.service;

import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.comment.repository.CommentRepository;
import onde.there.domain.Comment;
import onde.there.domain.Journey;
import onde.there.domain.Place;
import onde.there.domain.PlaceHeart;
import onde.there.domain.PlaceImage;
import onde.there.domain.type.PlaceCategoryType;
import onde.there.dto.place.PlaceDto;
import onde.there.dto.place.PlaceDto.Response;
import onde.there.dto.place.PlaceDto.UpdateRequest;
import onde.there.image.service.AwsS3Service;
import onde.there.journey.repository.JourneyRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceHeartRepository;
import onde.there.place.repository.PlaceImageRepository;
import onde.there.place.repository.PlaceRepository;
import onde.there.place.repository.PlaceRepositoryCustomImpl;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final JourneyRepository journeyRepository;
	private final PlaceRepository placeRepository;
	private final PlaceImageRepository placeImageRepository;
	private final PlaceHeartRepository placeHeartRepository;
	private final CommentRepository commentRepository;
	private final PlaceRepositoryCustomImpl placeRepositoryCustom;
	private final AwsS3Service awsS3Service;

	@Transactional
	public Place createPlace(List<MultipartFile> images, PlaceDto.CreateRequest request,
		String memberId) {
		log.info("createPlace : 장소 생성 시작!");
		Journey journey = journeyRepository.findById(request.getJourneyId())
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		if (!journey.getMember().getId().equals(memberId)) {
			throw new PlaceException(PlaceErrorCode.MISMATCH_MEMBER_ID);
		}

		Place place = request.toEntity();
		place.setJourney(journey);
		Place savePlace = placeRepository.save(place);

		List<String> imageUrls = imageUploadToS3(images);
		savePlaceImage(savePlace, imageUrls);
		log.info("createPlace : 장소 저장 완료! (장소 아이디 : " + savePlace.getId() + ")");

		return savePlace;
	}

	public PlaceDto.Response getPlace(Long placeId) {
		log.info("getPlace : 장소 조회 시작! (장소 아이디 : " + placeId + ")");
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));
		Hibernate.initialize(place.getPlaceImages());
		Response response = Response.toResponse(place);

		log.info("getPlace : 장소 조회 완료! (장소 아이디 : " + placeId + ")");
		return response;
	}

	public List<Response> list(Long journeyId, String memberId) {
		log.info("list : 여정에 포함된 장소 조회 시작! (여정 아이디 : " + journeyId + ")");
		journeyRepository.findById(journeyId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		if (memberId == null) {
			List<Place> places = placeRepository.findAllByJourneyIdOrderByPlaceTimeAsc(journeyId);
			return Response.toResponse(places);
		}

		List<Tuple> tuples = placeRepositoryCustom.findAllByJourneyOrderByPlaceTimeAsc(
			journeyId, memberId);

		List<Response> responses = new ArrayList<>();
		for (Tuple tuple : tuples) {
			Place place = tuple.get(0, Place.class);
			boolean heartedCheck = Boolean.TRUE.equals(tuple.get(1, Boolean.class));

			if (place != null) {
				Response response = Response.toResponse(place);
				response.setHeartedCheck(heartedCheck);
				responses.add(response);
			}
		}

		log.info("list : 여정에 포함된 장소 조회 완료! (여정 아이디 : " + journeyId + ")");
		return responses;
	}

	@Transactional
	public boolean delete(Long placeId, String memberId) {
		log.info("delete : 장소 삭제 시작! (장소 아이디 : " + placeId + ")");
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));

		if (!place.getJourney().getMember().getId().equals(memberId)) {
			throw new PlaceException(PlaceErrorCode.MISMATCH_MEMBER_ID);
		}

		deleteAllWithPlaceRelations(placeId);

		placeRepository.delete(place);
		log.info("delete : 장소 삭제 완료! (장소 아이디 : " + placeId + ")");
		return true;
	}

	@Transactional
	public boolean deleteAll(Long journeyId, String memberId) {
		log.info("deleteAll : 여정에 포함된 장소 삭제 시작! (여정 아이디 : " + journeyId + ")");
		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		if (!journey.getMember().getId().equals(memberId)) {
			throw new PlaceException(PlaceErrorCode.MISMATCH_MEMBER_ID);
		}

		List<Place> places = placeRepository.findAllByJourneyIdOrderByPlaceTimeAsc(journeyId);

		if (places.size() == 0) {
			throw new PlaceException(PlaceErrorCode.DELETED_NOTING);
		}

		for (Place place : places) {
			deletePlaceImagesInPlace(place.getId());
		}

		placeRepository.deleteAll(places);
		log.info("deleteAll : 여정에 포함된 장소 삭제 완료! (여정 아이디 : " + journeyId + ")");
		return true;
	}

	@Transactional
	public PlaceDto.Response updatePlace(List<MultipartFile> multipartFile, UpdateRequest request,
		String memberId) {
		log.info("updatePlace : 장소 업데이트 시작! (장소 아이디 : " + request.getPlaceId() + ")");
		Place savedPlace = placeRepository.findById(request.getPlaceId())
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));

		if (!savedPlace.getJourney().getMember().getId().equals(memberId)) {
			throw new PlaceException(PlaceErrorCode.MISMATCH_MEMBER_ID);
		}

		log.info("장소에 이미지 제외한 값 업데이트 시작! (장소 아이디 : " + request.getPlaceId() + ")");
		Place updatePlace = setUpdateRequest(savedPlace, request);
		placeRepository.save(updatePlace);
		log.info("장소에 이미지 제외한 값 업데이트 완료! (장소 아이디 : " + request.getPlaceId() + ")");

		deletePlaceImagesInPlace(request.getPlaceId());

		List<String> updateUrls = imageUploadToS3(multipartFile);
		savePlaceImage(updatePlace, updateUrls);

		Response response = Response.toResponse(savedPlace);
		response.setImageUrls(updateUrls);

		log.info("updatePlace : 장소 업데이트 완료! (장소 아이디 : " + request.getPlaceId() + ")");
		return response;
	}

	private void deleteAllWithPlaceRelations(Long placeId) {
		log.info("deleteAllWithPlaceRelations : 장소 연관관계 삭제 삭제 시작! (장소 아이디 : " + placeId + ")");
		deletePlaceImagesInPlace(placeId);
		deletePlaceCommentInPlace(placeId);
		deletePlaceHeartInPlace(placeId);
		log.info("deleteAllWithPlaceRelations : 장소 연관관계 삭제 삭제 완료! (장소 아이디 : " + placeId + ")");
	}

	private void deletePlaceHeartInPlace(Long placeId) {
		log.info("deletePlaceHeartInPlace : 장소 좋아요 삭제 삭제 시작! (장소 아이디 : " + placeId + ")");
		placeHeartRepository.deleteAll(placeHeartRepository.findAllByPlaceId(placeId));
		log.info("deletePlaceHeartInPlace : 장소 좋아요 삭제 삭제 완료! (장소 아이디 : " + placeId + ")");
	}

	private void deletePlaceCommentInPlace(Long placeId) {
		log.info("deletePlaceCommentInPlace : 장소 댓글 삭제 삭제 시작! (장소 아이디 : " + placeId + ")");
		commentRepository.deleteAll(commentRepository.findAllByPlaceId(placeId));
		log.info("deletePlaceCommentInPlace : 장소 댓글 삭제 삭제 완료! (장소 아이디 : " + placeId + ")");
	}

	private void deletePlaceImagesInPlace(Long placeId) {
		log.info("deletePlaceImages : 장소에 포함된 이미지 삭제 시작! (장소 아이디 : " + placeId + ")");
		List<PlaceImage> placeImages = placeImageRepository.findAllByPlaceId(placeId);
		for (PlaceImage placeImage : placeImages) {
			awsS3Service.deleteFile(placeImage.getUrl());
			placeImageRepository.delete(placeImage);
		}
		log.info("deletePlaceImages : 장소에 포함된 이미지 삭제 완료! (장소 아이디 : " + placeId + ")");
	}

	private List<String> imageUploadToS3(List<MultipartFile> images) {
		return awsS3Service.uploadFiles(images);
	}

	private void savePlaceImage(Place savePlace, List<String> imageUrls) {
		List<PlaceImage> placeImages = new ArrayList<>();
		for (String imageUrl : imageUrls) {
			log.info("savePlaceImage : 장소 이미지 저장 시작! (장소 이미지 URL : " + imageUrl + ")");
			placeImages.add(placeImageRepository.save(new PlaceImage(savePlace, imageUrl)));
			log.info("savePlaceImage : 장소 이미지 저장 완료! (장소 이미지 URL : " + imageUrl + ")");
		}
		savePlace.setPlaceImages(placeImages);
	}

	private Place setUpdateRequest(Place savePlace, PlaceDto.UpdateRequest updateRequest) {
		savePlace.setLatitude(updateRequest.getLatitude());
		savePlace.setLongitude(updateRequest.getLongitude());
		savePlace.setTitle(updateRequest.getTitle());
		savePlace.setText(updateRequest.getText());
		savePlace.setAddressName(updateRequest.getAddressName());
		savePlace.setRegion1(updateRequest.getRegion1());
		savePlace.setRegion2(updateRequest.getRegion2());
		savePlace.setRegion3(updateRequest.getRegion3());
		savePlace.setRegion4(updateRequest.getRegion4());
		savePlace.setPlaceName(updateRequest.getPlaceName());
		savePlace.setPlaceTime(updateRequest.getPlaceTime());
		savePlace.setPlaceCategory(
			PlaceCategoryType.toPlaceCategoryType(updateRequest.getPlaceCategory()));
		return savePlace;
	}
}
