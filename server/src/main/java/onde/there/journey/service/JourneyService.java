package onde.there.journey.service;

import static onde.there.domain.type.RegionType.findByRegion;
import static onde.there.journey.exception.JourneyErrorCode.AVAILABLE_AFTER_LONGIN;
import static onde.there.journey.exception.JourneyErrorCode.DATE_ERROR;
import static onde.there.journey.exception.JourneyErrorCode.NOT_FOUND_JOURNEY;
import static onde.there.journey.exception.JourneyErrorCode.NOT_FOUND_MEMBER;
import static onde.there.journey.exception.JourneyErrorCode.YOU_ARE_NOT_THE_AUTHOR;

import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Journey;
import onde.there.domain.JourneyTheme;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.type.JourneyThemeType;
import onde.there.dto.journy.JourneyDto;
import onde.there.dto.journy.JourneyDto.DetailResponse;
import onde.there.dto.journy.JourneyDto.FilteringRequest;
import onde.there.dto.journy.JourneyDto.FilteringResponse;
import onde.there.dto.journy.JourneyDto.JourneyListResponse;
import onde.there.dto.journy.JourneyDto.MyListResponse;
import onde.there.dto.journy.JourneyDto.NickNameListResponse;
import onde.there.dto.journy.JourneyDto.UpdateRequest;
import onde.there.dto.journy.JourneyDto.UpdateResponse;
import onde.there.image.service.AwsS3Service;
import onde.there.journey.exception.JourneyException;
import onde.there.journey.repository.JourneyBookmarkRepositoryCustom;
import onde.there.journey.repository.JourneyRepository;
import onde.there.journey.repository.JourneyThemeRepository;
import onde.there.member.repository.MemberRepository;
import onde.there.place.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyService {

	private final JourneyRepository journeyRepository;
	private final JourneyThemeRepository journeyThemeRepository;
	private final MemberRepository memberRepository;
	private final AwsS3Service awsS3Service;
	private final PlaceRepository placeRepository;
	private final JourneyBookmarkRepositoryCustom bookmarkRepositoryCustom;

	@Transactional
	public JourneyDto.CreateResponse createJourney(
		JourneyDto.CreateRequest request, MultipartFile thumbnail,
		String memberId) {

		log.info("createJourney() : 호출");

		verifyJwt(memberId);
		Member checkMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_MEMBER));

		if (request.getEndDate().isBefore(request.getStartDate())) {
			throw new JourneyException(DATE_ERROR);
		}

		List<String> imageUrls = awsS3Service.uploadFiles(
			Collections.singletonList(thumbnail));

		log.info("createJourney() : 여정 thumbnail 업로드 완료, "
			+ "(여정 thumbnail URL : " + imageUrls.get(0) + ")");

		Journey journey = Journey.builder()
			.member(checkMember)
			.title(request.getTitle())
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.journeyThumbnailUrl(imageUrls.get(0))
			.disclosure(request.getDisclosure())
			.introductionText(request.getIntroductionText())
			.numberOfPeople(request.getNumberOfPeople())
			.region(findByRegion(request.getRegion()))
			.build();

		journeyRepository.save(journey);
		log.info("createJourney() : 여정 생성 완료,  JourneyId : " + journey.getId());

		List<String> inputJourneyThemes = request.getJourneyThemes();
		for (String inputJourneyTheme : inputJourneyThemes) {
			JourneyTheme journeyTheme = JourneyTheme.builder()
				.journey(journey)
				.journeyThemeName(
					JourneyThemeType.findByTheme(inputJourneyTheme))
				.build();
			journeyThemeRepository.save(journeyTheme);
			log.info(
				"createJourney() : journeyTheme 생성 완료, journeyThemeId : "
					+ journeyTheme.getId());
		}

		log.info("createJourney() : 종료");
		return JourneyDto.CreateResponse.fromEntity(journey,
			inputJourneyThemes);
	}

	public List<JourneyDto.JourneyListResponse> list() {

		log.info("list() : 호출");

		List<JourneyDto.JourneyListResponse> list = new ArrayList<>();
		List<Journey> journeyList = journeyRepository
			.findAllByDisclosure("public");

		log.info("list() : 종료");

		return getList(list, journeyList);
	}

	@Transactional
	public Page<JourneyDto.MyListResponse> myList(
		String memberId, Pageable pageable) {

		log.info("myList() : 호출");

		verifyJwt(memberId);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_MEMBER));

		Page<Journey> journeys = journeyRepository.journeyListByMemberId(
			memberId, pageable);

		log.info("myList() : 조회 완료");

		return journeys.map(
			MyListResponse::fromEntity
		);
	}

	@Transactional
	public Page<JourneyDto.NickNameListResponse> nickNameList(
		String nickname, Pageable pageable, String memberId) {

		log.info("myList() : 호출");

		Member member = memberRepository.findByNickName(nickname)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_MEMBER));

		Page<Journey> journeys = journeyRepository.journeyListByMemberId(
			member.getId(), pageable);

		List<Journey> journeyList = journeys.getContent();

		Page<NickNameListResponse> nickNameListResponses = journeys.map(
			NickNameListResponse::fromEntity
		);

		if (memberId == null) {
			for (int i = 0; i < nickNameListResponses.getContent().size();
				i++) {
				nickNameListResponses.getContent().get(i).setBookmark(false);
			}
		} else {

			List<Boolean> isBookmarks = getBookmarks(memberId, journeyList);
			for (int i = 0; i < nickNameListResponses.getContent().size();
				i++) {
				nickNameListResponses.getContent().get(i)
					.setBookmark(isBookmarks.get(i));
			}
		}

		log.info("myList() : 조회 완료");

		return nickNameListResponses;
	}

	@Transactional
	public Page<FilteringResponse> filteredList(
		FilteringRequest filteringRequest, Pageable pageable,
		String memberId) {

		log.info("filteredList() : 호출");

		Page<Journey> journeys = journeyRepository.searchAll(filteringRequest,
			pageable);

		List<Journey> journeyList = journeys.getContent();
		Page<FilteringResponse> filteringResponses = journeys.map(
			FilteringResponse::fromEntity);

		if (memberId == null) {
			for (int i = 0; i < filteringResponses.getContent().size(); i++) {
				filteringResponses.getContent().get(i).setBookmark(false);
			}
		} else {

			List<Boolean> isBookmarks = getBookmarks(memberId, journeyList);

			for (int i = 0; i < filteringResponses.getContent().size(); i++) {
				filteringResponses.getContent().get(i)
					.setBookmark(isBookmarks.get(i));
			}
		}

		log.info("filteredList() : 종료");

		return filteringResponses;

	}

	private List<JourneyListResponse> getList(List<JourneyListResponse> list,
		List<Journey> journeyList) {

		log.info("getList() : 호출");

		for (Journey journey : journeyList) {
			List<String> journeyThemeTypeList = journeyThemeRepository
				.findAllByJourneyId(journey.getId())
				.stream().map(journeyTheme -> journeyTheme
					.getJourneyThemeName()
					.getThemeName())
				.collect(Collectors.toList());

			list.add(
				JourneyListResponse.fromEntity(journey, journeyThemeTypeList));
		}

		log.info("getList() : 종료");

		return list;
	}

	public DetailResponse journeyDetail(Long journeyId, String memberId) {

		log.info("journeyDetail() : 호출");

		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		List<String> journeyThemeTypeList = journeyThemeRepository
			.findAllByJourneyId(journey.getId())
			.stream().map(journeyTheme -> journeyTheme
				.getJourneyThemeName()
				.getThemeName())
			.collect(Collectors.toList());

		DetailResponse detailResponse = DetailResponse
			.fromEntity(journey, journeyThemeTypeList);

		log.info(
			"journeyDetail() : journey 조회 완료, journeyId : " + journey.getId());

		if (memberId == null) {
			detailResponse.setBookmark(false);
		} else {
			Tuple tuple = bookmarkRepositoryCustom.bookmarkConfirmation(
				journey.getId(), memberId);
			boolean isBookmark = Boolean.TRUE.equals(
				tuple.get(1, Boolean.class));
			detailResponse.setBookmark(isBookmark);
		}

		log.info("journeyDetail() : 종료");

		return detailResponse;
	}

	@Transactional
	public void deleteJourney(Long journeyId, String memberId) {

		log.info("deleteJourney() : 호출");

		verifyJwt(memberId);
		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		String Author = journey.getMember().getId();
		if (!Objects.equals(Author, memberId)) {
			throw new JourneyException(YOU_ARE_NOT_THE_AUTHOR);
		}

		List<JourneyTheme> journeyThemeTypeList = journeyThemeRepository
			.findAllByJourneyId(journey.getId());

		List<Place> list = placeRepository.findAllByJourney(journey);

		placeRepository.deleteAll(list);
		awsS3Service.deleteFile(journey.getJourneyThumbnailUrl());
		journeyThemeRepository.deleteAll(journeyThemeTypeList);
		journeyRepository.delete(journey);

		log.info("deleteJourney() : 여정 삭제 완료, journeyId : " + journey.getId());
		log.info("deleteJourney() : 종료");

	}

	@Transactional
	public UpdateResponse updateJourney(UpdateRequest request,
		MultipartFile thumbnail, String memberId) {

		log.info("updateJourney() : 호출" + thumbnail);

		verifyJwt(memberId);
		Journey journey = journeyRepository.findById(request.getJourneyId())
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		String Author = journey.getMember().getId();
		if (!Objects.equals(Author, memberId)) {
			throw new JourneyException(YOU_ARE_NOT_THE_AUTHOR);
		}

		List<JourneyTheme> journeyThemes = journeyThemeRepository
			.findAllByJourneyId(journey.getId());

		journeyThemeRepository.deleteAll(journeyThemes);
		List<String> inputJourneyThemes = request.getJourneyThemes();
		for (String inputJourneyTheme : inputJourneyThemes) {
			JourneyTheme journeyTheme = JourneyTheme.builder()
				.journey(journey)
				.journeyThemeName(
					JourneyThemeType.findByTheme(inputJourneyTheme))
				.build();
			journeyThemeRepository.save(journeyTheme);
		}
		log.info("updateJourney() : journeyTheme 수정 완료");


		String imageUrl = updateThumbnailUrl(thumbnail, journey);
		journey.setJourneyThumbnailUrl(imageUrl);
		journey.setTitle(request.getTitle());
		journey.setStartDate(request.getStartDate());
		journey.setEndDate(request.getEndDate());
		journey.setNumberOfPeople(request.getNumberOfPeople());
		journey.setDisclosure(request.getDisclosure());
		journey.setIntroductionText(request.getIntroductionText());
		journey.setRegion(findByRegion(request.getRegion()));
		log.info("updateJourney() : journey 수정 완료, journeyId : "
			+ journey.getId());

		log.info("updateJourney() : 종료");

		return JourneyDto.UpdateResponse
			.fromEntity(journey, inputJourneyThemes);
	}

	private List<Boolean> getBookmarks(String memberId,
		List<Journey> journeyList) {
		List<Boolean> isBookmarks = new ArrayList<>();
		for (Journey journey : journeyList) {
			Tuple tuple = bookmarkRepositoryCustom.bookmarkConfirmation(
				journey.getId(), memberId);
			boolean isBookmark = Boolean.TRUE.equals(
				tuple.get(1, Boolean.class));
			isBookmarks.add(isBookmark);
		}
		return isBookmarks;
	}

	private void verifyJwt(String memberId) {
		if (memberId == null) {
			throw new JourneyException(AVAILABLE_AFTER_LONGIN);
		}
	}

	private String updateThumbnailUrl(MultipartFile multipartFile, Journey journey) {
		boolean condition = multipartFile == null || multipartFile.isEmpty();
		return condition ? journey.getJourneyThumbnailUrl() : awsS3Service.uploadFiles(List.of(multipartFile)).get(0);
	}
}
