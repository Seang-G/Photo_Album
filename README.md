# 포토앨범 🖼️

### 프로젝트 개요
##### 소개
- 단순한 포토앨범 웹 서비스입니다.<br />자신만의 포토앨범을 만들고 사진을 저장하세요!
##### 목적
- 웹 기술 학습 (Spring Boot, React, MySQL)
- 웹 기술 간의 상호작용 학습
##### 참여 인원
- 1인
##### 사용한 기술 스택
| 용도     | 기술 스택 |
|:--------:|:--------:|
| Front End    | React.js     |
| Back End     | Spring Boot  |
| Database     | MySQL        |
| Server Hosting     | AWS(예정)        |

<br />

### 기능 소개

##### 메인
![메인](https://github.com/Seang-G/Photo_Album/assets/61152284/890d1c8d-d766-4ab4-b6a9-615db8d2ec05)
- 서비스를 시작하면 가장 먼저 보이는 페이지
- 로그인 (아이디 저장 가능)
  
<br />

##### 회원가입
![회원가입](https://github.com/Seang-G/Photo_Album/assets/61152284/ab77db1f-2317-42f3-8f04-abc53e7a76fd)
- 회원가입을 할 수 있는 페이지
- 몇 가지 규칙 확인
  - 이메일 형식 
  - 닉네임 중복 여부, 2글자 이상
  - 비밀번호 알파벳, 숫자, 특수문자 포함 여부, 8~30 글자
    
<br />

##### 사진첩
![사진첩(썸네일)](https://github.com/Seang-G/Photo_Album/assets/61152284/aef7b87c-d3fb-4288-b768-e25de0a22f2d)
- 사진첩 페이지
- 앨범 생성
- 앨범내 사진 썸네일 확인
- 생성 날짜순, 이름순으로 정렬 (오름차순, 내림차순)
- 앨범 검색

<br />

##### 앨범
![앨범](https://github.com/Seang-G/Photo_Album/assets/61152284/92d8a68f-8c17-47da-abcf-6efa114d3898)
- 앨범 내 사진들을 볼 수 있는 페이지
- 사진 추가
- 사진 삭제, 다운로드, 다른 페이지로 이동 (복수 선택 가능)
- 생성 날짜순, 이름순으로 정렬 가능 (오름차순, 내림차순)
- 앨범명 변경
- 앨범 생성 날짜, 사진 장 수 확인

<br />

##### 세부 정보
![사진 세부정보](https://github.com/Seang-G/Photo_Album/assets/61152284/68819467-e9df-4d13-92ff-bc751f492aec)
- 사진의 세부정보를 확인할 수 있는 페이지
- 파일명, 업로드 날짜, 파일 용량 확인
- 사진 삭제, 다운로드, 다른 앨범으로 이동

<br />

##### 사진 추가
![사진 추가](https://github.com/Seang-G/Photo_Album/assets/61152284/8cb43c42-5bf9-4071-8723-54ba4221c63c)
- 사진을 추가할 수 있는 페이지
- 사진을 드래그해서 추가
- 여러 사진 한꺼번에 추가
- 업로드 가능한 사진 용량 제한 (각 10MB)
