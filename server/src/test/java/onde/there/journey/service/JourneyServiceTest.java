//package onde.there.journey.service;
//
//import static onde.there.journey.exception.JourneyErrorCode.THERE_IS_NO_MATCHING_THEME;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import javax.persistence.EntityManager;
//import onde.there.domain.Journey;
//import onde.there.domain.JourneyTheme;
//import onde.there.domain.Member;
//import onde.there.domain.type.JourneyThemeType;
//import onde.there.domain.type.RegionType;
//import onde.there.dto.journy.JourneyDto;
//import onde.there.dto.journy.JourneyDto.CreateRequest;
//import onde.there.dto.journy.JourneyDto.CreateResponse;
//import onde.there.dto.journy.JourneyDto.JourneyListResponse;
//import onde.there.image.service.AwsS3Service;
//import onde.there.journey.exception.JourneyErrorCode;
//import onde.there.journey.exception.JourneyException;
//import onde.there.journey.repository.JourneyRepository;
//import onde.there.journey.repository.JourneyRepositoryCustom;
//import onde.there.journey.repository.JourneyThemeRepository;
//import onde.there.member.repository.MemberRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Pageable;
//import org.springframework.mock.web.MockMultipartFile;
//
//@ExtendWith(MockitoExtension.class)
//public class JourneyServiceTest {
//
//	@Mock
//	private MemberRepository memberRepository;
//
//	@Mock
//	private JourneyRepository journeyRepository;
//
//	@Mock
//	private JourneyThemeRepository journeyThemeRepository;
//
//	@Mock
//	private JourneyRepositoryCustom journeyRepositoryCustom;
//
//	@Mock
//	private AwsS3Service awsS3Service;
//
//	@Mock
//	private EntityManager entityManager;
//
//	@InjectMocks
//	private JourneyService journeyService;
//
//	@Test
//	void createJourneySuccess() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//
//		given(memberRepository.findById(anyString()))
//			.willReturn(Optional.of(member));
//
//		String fileName = "1";
//		String contentType = "png";
//		String filePath = "src/main/resources/testImages/1.png";
//		FileInputStream fileInputStream = new FileInputStream(
//			new File(filePath));
//		MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName,
//			fileName + "." + contentType, contentType, fileInputStream);
//
//		given(awsS3Service.uploadFiles(any()))
//			.willReturn(new ArrayList<>(Arrays.asList("testUrl")));
//
//		ArgumentCaptor<Journey> journeyCaptor = ArgumentCaptor.forClass(
//			Journey.class);
//		ArgumentCaptor<JourneyTheme> journeyThemeCaptor = ArgumentCaptor.forClass(
//			JourneyTheme.class);
//
//		CreateResponse journeyDto = journeyService.createJourney(
//			CreateRequest.builder()
//				.memberId("tHereId")
//				.title("TitleTest")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.journeyThemes(Arrays.asList("힐링", "식도락"))
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region("서울")
//				.build(),
//			mockMultipartFile,
//			"tHereId"
//		);
//
//		verify(journeyRepository, times(1))
//			.save(journeyCaptor.capture());
//		verify(journeyThemeRepository, times(2))
//			.save(journeyThemeCaptor.capture());
//
//		assertEquals("testNickname", journeyDto.getNickName());
//		assertEquals("TitleTest", journeyDto.getTitle());
//		assertEquals(LocalDate.parse("2022-10-16"),
//			journeyDto.getStartDate());
//		assertEquals(LocalDate.parse("2022-10-17"),
//			journeyDto.getEndDate());
//		assertNotNull(journeyDto.getJourneyThumbnailUrl());
//		assertEquals(JourneyThemeType.HEALING,
//			journeyThemeCaptor.getAllValues().get(0).getJourneyThemeName());
//		assertEquals(JourneyThemeType.RESTAURANT,
//			journeyThemeCaptor.getAllValues().get(1).getJourneyThemeName());
//		assertEquals("서울", journeyDto.getRegion());
//
//	}
//
//	@Test
//	@DisplayName("종료 날짜가 시작 날짜보다 과거 - 여정 생성 실패")
//	void createJourney_DateError() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//
//		given(memberRepository.findById(anyString()))
//			.willReturn(Optional.of(member));
//
//		FileInputStream fis = new FileInputStream(
//			"src/main/resources/testImages/" + "1.png");
//		MockMultipartFile testThumbnail = new MockMultipartFile("1", "1.png",
//			"png", fis);
//
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.createJourney(
//				CreateRequest.builder()
//					.memberId("tHereEmail")
//					.title("TitleTest")
//					.startDate(LocalDate.parse("2022-10-16"))
//					.endDate(LocalDate.parse("2022-10-15"))
//					.disclosure("public")
//					.journeyThemes(Arrays.asList("힐링", "식도락"))
//					.introductionText("테스트 소개 글")
//					.numberOfPeople(7)
//					.region("서울")
//					.build(),
//				testThumbnail,
//				"tHereId"
//			));
//
//		assertEquals(JourneyErrorCode.DATE_ERROR, exception.getErrorCode());
//	}
//
//	@Test
//	@DisplayName("일치하는 테마가 없을 때 - 여정 생성 실패")
//	void createJourney_THERE_IS_NO_MATCHING_THEME() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//
//		given(memberRepository.findById(anyString()))
//			.willReturn(Optional.of(member));
//
//		String fileName = "1";
//		String contentType = "png";
//		String filePath = "src/main/resources/testImages/1.png";
//		FileInputStream fileInputStream = new FileInputStream(
//			new File(filePath));
//		MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName,
//			fileName + "." + contentType, contentType, fileInputStream);
//
//		given(awsS3Service.uploadFiles(any()))
//			.willReturn(new ArrayList<>(Arrays.asList("testUrl")));
//
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.createJourney(
//				JourneyDto.CreateRequest.builder()
//					.memberId("tHereEmail")
//					.title("TitleTest")
//					.startDate(LocalDate.parse("2022-10-16"))
//					.endDate(LocalDate.parse("2022-10-17"))
//					.disclosure("public")
//					.journeyThemes(Arrays.asList("testTheme1", "testTheme2"))
//					.introductionText("테스트 소개 글")
//					.numberOfPeople(7)
//					.region("서울")
//					.build(),
//				mockMultipartFile,
//				"tHereId"
//			));
//
//		assertEquals(THERE_IS_NO_MATCHING_THEME, exception.getErrorCode());
//	}
//
//	@Test
//	@DisplayName("일치하는 region 없을 때 - 여정 생성 실패")
//	void createJourney_NO_AREA_MATCHES() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//
//		given(memberRepository.findById(anyString()))
//			.willReturn(Optional.of(member));
//
//		String fileName = "1";
//		String contentType = "png";
//		String filePath = "src/main/resources/testImages/1.png";
//		FileInputStream fileInputStream = new FileInputStream(
//			new File(filePath));
//		MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName,
//			fileName + "." + contentType, contentType, fileInputStream);
//
//		given(awsS3Service.uploadFiles(any()))
//			.willReturn(new ArrayList<>(Arrays.asList("testUrl")));
//
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.createJourney(
//				JourneyDto.CreateRequest.builder()
//					.memberId("tHereId")
//					.title("TitleTest")
//					.startDate(LocalDate.parse("2022-10-16"))
//					.endDate(LocalDate.parse("2022-10-17"))
//					.disclosure("public")
//					.journeyThemes(Arrays.asList("힐링", "식도락"))
//					.introductionText("테스트 소개 글")
//					.numberOfPeople(7)
//					.region("없는지역")
//					.build(),
//				mockMultipartFile,
//				"tHereId"
//			));
//
//		assertEquals(JourneyErrorCode.NO_REGION_MATCHES,
//			exception.getErrorCode());
//	}
//
//	@Test
//	void JourneyListSuccess() {
//		//given
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//
//		List<Journey> journeyList = Arrays.asList(
//			Journey.builder()
//				.id(1L)
//				.member(member)
//				.title("TitleTest")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region(RegionType.SEOUL)
//				.build(),
//			Journey.builder()
//				.id(2L)
//				.member(member)
//				.title("TitleTest2")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region(RegionType.GYEONGGI)
//				.build()
//		);
//
//		List<JourneyTheme> journeyTheme = Arrays.asList(
//			JourneyTheme.builder()
//				.id(1L)
//				.journey(journeyList.get(0))
//				.journeyThemeName(JourneyThemeType.HEALING)
//				.build(),
//			JourneyTheme.builder()
//				.id(1L)
//				.journey(journeyList.get(0))
//				.journeyThemeName(JourneyThemeType.RESTAURANT)
//				.build()
//		);
//
//		given(journeyRepository.findAllByDisclosure(anyString()))
//			.willReturn(journeyList);
//		given(journeyThemeRepository.findAllByJourneyId(any()))
//			.willReturn(journeyTheme);
//
//		//when
//		List<JourneyListResponse> list = journeyService.list();
//
//		//then
//		assertEquals(2, list.size());
//		assertEquals(1L, list.get(0).getJourneyId());
//		assertEquals(2L, list.get(1).getJourneyId());
//		assertEquals("TitleTest", list.get(0).getTitle());
//		assertEquals("TitleTest2", list.get(1).getTitle());
//		assertEquals(Arrays.asList("힐링", "식도락"),
//			list.get(0).getJourneyThemes());
//		assertEquals(Arrays.asList("힐링", "식도락"),
//			list.get(1).getJourneyThemes());
//	}
//
//	@Test
//	@DisplayName("존재하는 유저가 아닐 때 - 내 여정 조회 실패")
//	void MyJourneyList_NOT_FOUND_MEMBER() {
//		//given
//		given(memberRepository.findById(anyString()))
//			.willReturn(Optional.empty());
//
//		Pageable pageable = Pageable.ofSize(15);
//
//		//when
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.myList("test", pageable));
//
//		//then
//		assertEquals(JourneyErrorCode.NOT_FOUND_MEMBER,
//			exception.getErrorCode());
//
//	}
//
//
//}
