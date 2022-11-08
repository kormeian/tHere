package onde.there.exception;

import lombok.extern.slf4j.Slf4j;
import onde.there.exception.type.ErrorCode;
import onde.there.image.exception.ImageErrorResponse;
import onde.there.image.exception.ImageException;
import onde.there.journey.exception.JourneyErrorResponse;
import onde.there.journey.exception.JourneyException;
import onde.there.member.exception.MemberErrorResponse;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.place.exception.PlaceErrorResponse;
import onde.there.place.exception.PlaceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(
		MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST,
			e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
		log.error("MethodArgumentNotValidException " + e.getMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e) {
		ErrorResponse errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST,
			"Request Body가 비어 있습니다");
		log.error("HttpMessageNotReadableException " + errorResponse.getErrorCode());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(MemberException.class)
	public ResponseEntity<?> handleMemberException(MemberException e) {
		MemberErrorResponse errorResponse = new MemberErrorResponse(e.getMemberErrorCode(),
			e.getErrorMessage());

		log.error("MemberException " + e.getMemberErrorCode());
		if (errorResponse.getErrorCode() == MemberErrorCode.AUTHORITY_ERROR) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
		}
		return ResponseEntity.badRequest().body(errorResponse);
	}


	@ExceptionHandler(PlaceException.class)
	public ResponseEntity<?> handlerPlaceException(PlaceException e) {

		log.error("PlaceException " + e.getErrorCode());
		return ResponseEntity.badRequest()
			.body(PlaceErrorResponse.builder()
				.errorCode(e.getErrorCode())
				.errorMessage(e.getErrorMessage())
				.build());
	}

	@ExceptionHandler(ImageException.class)
	public ResponseEntity<?> handlerPlaceException(ImageException e) {

		log.error("ImageException " + e.getErrorCode());
		return ResponseEntity.badRequest()
			.body(ImageErrorResponse.builder()
				.errorCode(e.getErrorCode())
				.errorMessage(e.getErrorMessage())
				.build());
	}

	@ExceptionHandler(JourneyException.class)
	public ResponseEntity<?> handleJourneyException(JourneyException e) {
		log.error("{} is occurred.", e.getErrorCode());

		log.error("JourneyException " + e.getErrorCode());
		return ResponseEntity.badRequest()
			.body(new JourneyErrorResponse(e.getErrorCode(), e.getErrorMessage()));
	}
}
