# tHere

tHEre 여행을 하면서 찍은 사진을 여행 별로 저장 및 공유 할 수 있는 서비스입니다.  
여행 때 다녀온 장소를 지도에 표시하여 여행 경로를 쉽게 파악 할 수 있습니다.  
사용자는 여행 장소에 대하여 글을 남길 수 있고 댓글을 이용하여 소통을 할 수 있습니다.

![image](https://user-images.githubusercontent.com/68500898/201557974-15cb6357-0011-4c6e-88ec-69c35ff00c70.png)

## 프로젝트 기간
- 기획: 22.10.7 ~ 22.10.14
- 개발: 22.10.14 ~ 21.11.14

## 구성원
### 백엔드
- [이시화](https://github.com/roomdoor)
- [김이안](https://github.com/kormeian)
- [김정민](https://github.com/Jungmini0601)
- [서주원](https://github.com/Joowon-Seo)

### 프론트
- [김리안](https://github.com/lianKim)
- [한규빈](https://github.com/kyubhinhan)

## 프로젝트 목표
### 공통 목표
- 백엔드와 프론트엔드의 협업 및 원활한 의사소통 능력 향상
- 웹 애플리케이션의 기획부터 배포 경험 축적
- 서비스의 핵심 기능 구현을 통한 완성도있는 프로젝트 구현
### 백엔드 목표
- RESTful 한 API 구현
- 각 기능에 적합한 TEST 구현
- 도메인 분석 및 DB 설계

## Skills & Tools
## FrontEnd
<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"> <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/reactQuery-FF4154?style=for-the-badge&logo=react Query&logoColor=white"> <img src="https://img.shields.io/badge/vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"> <img src="https://img.shields.io/badge/Emotion-FE5196?style=for-the-badge&logo=Emotion&logoColor=white"> <img src="https://img.shields.io/badge/Recoil-0075EB?style=for-the-badge">

## BackEnd
- Java
- Spring Boot
    - Spring Security
    - Spring JWT
    - Spring batch
- JPA
- AWS
    - EC2
    - S3
- MySQL
- H2
- Redis
- Git
- Github
- Github Action
- Docker
- IntelliJ
- Slack
- Notion
- Gather

## API 문서
- swagger 를 활용하여 API 문서화하였습니다.
  [API 문서 링크](http://ec2-3-34-2-239.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html#/)


## 구현 기능

<table align="center">
  <thead>
    <tr margin-bottom=3px>
      <td width="300" align="center">
        <b style="color:#8fe3d9"> 수정 테스트<b>
      </td>
      <td width="300" align="center">
        <b>
          장소 카테고리 선택 기능
        </b>
      </td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/49369306/195571145-08304ede-6e58-4e18-8655-a788ef34eb63.gif" width="350">
      </td>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/108394338/201659547-0d874a0e-af56-4808-b12a-f26d336ab730.gif" width="350">
      </td>
    </tr>
    <tr>
      <td width="300" align="center">
        <b>
          사진 입력, 조회, 삭제 기능
        </b>
      </td>
      <td width="300" align="center">
        <b>
          편리한 시간 선택<br />(사진 입력 시 자동으로 시간 추천)
        </b>
      </td>
    </tr>
    <tr>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/108394338/201657042-b46832a6-e379-4f1d-810e-fb279f804008.gif" width="350"  > 
      </td>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/108394338/201658354-5d3e3432-20cd-4bfd-affa-a7a9766a6559.gif" width="350" >
      </td>
    </tr>
    <tr>
      <td width="300" align="center">
        <b>
          사진 입력 시 관련 장소 자동 추천 기능
        </b>
      </td>
      <td width="300" align="center">
        <b>
          장소 검색 기능
        </b>
      </td>
    </tr>
    <tr>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/108394338/201651973-cf8561cf-1f62-4a5a-a3ea-91619d232dfa.gif" width="350"  > 
      </td>
      <td width="300" align="center">
        <img src="https://user-images.githubusercontent.com/108394338/201650886-544ee779-0246-496e-a123-876269a59cbd.gif" width="350" >
      </td>
    </tr>
  </tbody>
</table>


<br><br>

### 여정
![여정_카테고리_검색](https://user-images.githubusercontent.com/68500898/201556167-7d13f5f3-514b-4d74-94bc-900663941397.gif)
- 장소 및 카테고리를 OR 조건으로 검색할 수 있습니다.
- 각 여정에는 북마크를 하여 빠르게 조회 하실 수 있습니다.

### 장소
![장소_조회](https://user-images.githubusercontent.com/68500898/201556298-198a1b09-5710-4346-bad5-24584a11079b.gif)
- 여정에 다녀왔던 장소들을 확인 하실 수 있습니다.
- 각 장소 역시 테마를 기반으로 검색이 가능합니다.
- 장소에 대하여 좋아요 및 댓글 작성이 가능합니다.

### 소셜 로그인
![소셜로그인](https://user-images.githubusercontent.com/68500898/201556356-0eac263b-c161-4aec-9723-cf6eb1117b5d.gif)
- 구글 소셜로그인을 제공합니다.

## Architecture
![image](https://user-images.githubusercontent.com/68500898/201556520-be86f647-5a86-42d2-9994-4cccaa34e1dc.png)
- 모든 서버는 AWS에 올라가 있습니다.
- 구동중인 서버: Redis Server, ONDe Server, MySQL(AWS RDS)Server, Amazon S3(이미지 저장소)

## CI/CD Pipeline
![image](https://user-images.githubusercontent.com/68500898/201556557-b53fa936-8283-4907-b5b3-c6aa3db2563c.png)
- 기능이 추가될 때 마다 수동 배포 하는 것에 불편함을 느끼고 CI/CD 파이프라인을 구축 했습니다.
- 진입장벽이 낮고, GitHub과 통합이 쉬운 GitHub Actions를 이용 했습니다.
- GitHub main 브렌치에 push 이벤트가 발생하면 CI/CD Pipeline이 동작하도록 구성 했습니다.

## ERD
![image](https://user-images.githubusercontent.com/68500898/201557057-874d2c5f-e856-4d66-9562-cab5350ed5b6.png)

# tHere
### [노션 링크](https://www.notion.so/ONDE-17658ae87a9b4ac6b8502b1dc276951d)
### [tHere 홈페이지](http://ec2-3-34-2-239.ap-northeast-2.compute.amazonaws.com)
