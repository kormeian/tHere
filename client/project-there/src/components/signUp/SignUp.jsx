import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import encodeFileToBase64 from '../../lib/utills/encodeFileToBase64';
import {
  checkEmailAPI,
  checkIdAPI,
  checkNickNameAPI,
  signupAPI,
} from '../../lib/utills/http';

const Wrapper = styled.div`
  display: flex;
  justify-content: center;
`;

const Form = styled.form`
  padding-top: 150px;
  font-size: var(--font-small);

  width: 330px;
  overflow: hidden;
`;

const InputLabel = styled.div`
  margin: 0 auto;
  margin-bottom: 4px;
  font-size: var(--font-micro);
  color: var(--color-gray500);
`;

const H2 = styled.h2`
  text-align: center;
  margin-bottom: 72px;
  font-size: var(--font-medium);
  font-weight: var(--weight-light);
`;

const SignUpButton = styled.button`
  display: block;
  margin: 48px auto 24px;
  padding: 8px 24px;
  font-size: var(--font-small);
  background-color: var(--color-green200);
  color: var(--color-gray100);
  border: none;
`;

const Row = styled.div`
  position: relative;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 48px;
`;

const TextInput = styled.input`
  width: 100%;
  padding: 8px 0;
  border: 0;
  border-bottom: 1px solid var(--color-green200);
  background: none;
  font-size: var(--font-small);

  &:-webkit-autofill {
    -webkit-box-shadow: 0 0 0 1000px var(--color-gray100) inset;
    box-shadow: 0 0 0 1000px var(--color-gray100) inset;
    -webkit-text-fill-color: var(--color-green200);
  }
`;

const CheckButton = styled.button`
  line-height: 1;
  padding: 8px 14px;
  position: absolute;
  right: 0;
  color: 0.5px solid var(--color-green200);
  border: 0.5px solid var(--color-green200);

  &:hover {
    cursor: pointer;
  }
`;

const ValidationErrMsg = styled.div`
  color: red;
  position: absolute;
  bottom: -21px;
  left: 0;
  font-size: var(--font-micro);
`;

const validationErrorMessage = {
  id: '영문/숫자, 1개 이상의 영문 포함 (4~60자)',
  password: '대문자/소문자/숫자/특수문자, 각각 1개 이상 포함 (10~20자)',
  passwordConfirm: '비밀번호가 일치하지 않습니다.',
  email: '잘못된 이메일 형식입니다.',
  name: '한글 (2~5자)',
  nickName: '한글/영문/숫자 (2~10자)',
};

// 미입력 값 확인 메세지
const emptyValueErrorMessage = {
  id: '아이디가 입력되지 않았습니다.',
  email: '이메일이 입력되지 않았습니다.',
  name: '이름이 입력되지 않았습니다.',
  nickName: '닉네임이 입력되지 않았습니다.',
  password: '비밀번호가 입력되지 않았습니다.',
  passwordConfirm: '비밀번호 확인이 입력되지 않았습니다.',
};

function SignUp({ info }) {
  // 아이디, 이메일 인증 완료 시에만 데이터 전송 가능
  const [checkId, setCheckId] = useState(false);
  const [checkEmail, setCheckEmail] = useState(false);
  const [checkNickName, setCheckNickName] = useState(false);

  // 유효성 검사 False 메시지
  const [idMessage, setIdMessage] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [emailMessage, setEmailMessage] = useState('');
  const [nameMessage, setNameMessage] = useState('');
  const [nickNameMessage, setNickNameMessage] = useState('');

  // 폼 데이터 상태 관리
  const [userForm, setUserForm] = useState({
    id: '',
    password: '',
    passwordConfirm: '',
    email: '',
    name: '',
    nickName: '',
    // profileImage: null,
  });

  useEffect(() => {
    if (!info) return;

    console.log(info.name);
    console.log(info.email);

    setUserForm((prev) => ({
      ...prev,
      name: info.name,
      email: info.email,
    }));
  }, []);

  // 이메일 인증 요청 성공 시 로그인 페이지로 이동해주는 함수
  const navigate = useNavigate();

  // 폼 데이터 값이 변경되면 userForm 값 업데이트
  const handleChangeForm = ({ target }) => {
    setUserForm((prev) => ({
      ...prev,
      [target.name]: target.value,
    }));
  };

  const checkValidation = (target, regex, callback) => {
    if (!regex.test(target.value)) {
      callback(validationErrorMessage[target.name]);
    } else {
      callback('');
    }
  };

  // 아이디 유효성 검사
  const handleIdValidation = ({ target }) => {
    const idRegex = /^(?=.*[a-zA-Z])[0-9a-zA-Z]{4,60}$/;
    checkValidation(target, idRegex, setIdMessage);
  };

  // 비밀번호 유효성 검사
  const handlePasswordValidation = ({ target }) => {
    const passwordRegex =
      /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{10,20}$/;
    checkValidation(target, passwordRegex, setPasswordMessage);
  };

  // 비밀번호 재확인 일치 여부 검사
  const handlePasswordConfirmValidation = ({ target }) => {
    if (userForm.password !== target.value) {
      setPasswordConfirmMessage('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordConfirmMessage('');
    }
  };

  // 이메일 유효성 검사
  const handleEmailValidation = ({ target }) => {
    const emailRegex =
      /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
    checkValidation(target, emailRegex, setEmailMessage);
  };

  // 이름 유효성 검사
  const handlNameValidation = ({ target }) => {
    const nameRegex = /^[가-힣]{2,5}$/;
    checkValidation(target, nameRegex, setNameMessage);
  };

  // 닉네임 유효성 검사
  const handlNickNameValidation = ({ target }) => {
    const nickNameRegex = /^[0-9a-zA-Z가-힣]{2,10}$/;
    checkValidation(target, nickNameRegex, setNickNameMessage);
  };

  const handleClickSignUp = async (e) => {
    e.preventDefault();

    console.log(userForm);

    // 미입력 값 체크
    const userFormKeys = Object.keys(userForm);
    for (let i = 0; i < userFormKeys.length; i += 1) {
      const item = userFormKeys[i];
      if (!userForm[item]) {
        return alert(emptyValueErrorMessage[item]);
      }
    }

    // 유효성 검사
    if (idMessage) {
      return alert('잘못된 아이디 형식입니다.');
    }

    if (passwordMessage) {
      return alert('잘못된 비밀번호 형식입니다.');
    }

    if (passwordConfirmMessage) {
      return alert('비밀번호가 일치하지 않습니다.');
    }

    if (emailMessage) {
      return alert('잘못된 이메일 형식입니다.');
    }

    if (nameMessage) {
      return alert('잘못된 이름 형식입니다.');
    }

    if (nickNameMessage) {
      return alert('잘못된 닉네임 형식입니다.');
    }

    // 아이디, 이메일 중복 검사
    if (!checkId) {
      return alert('아이디 중복 확인을 완료해주세요!');
    }

    if (!checkEmail) {
      return alert('이메일 중복 확인을 완료해주세요');
    }

    // // 닉네임 중복 확인도 추가
    // if (!checkNickName) {
    //   return alert('닉네임 중복 확인을 완료해주세요');
    // }

    try {
      const { message, email } = await signupAPI(userForm);

      alert(`${message} 이메일:${email}`);
      navigate('/signin');
    } catch (err) {
      const { errCode, errMessage } = err.response.data;
      alert(errMessage);
      setCheckId(false);
      setCheckEmail(false);
    }
  };

  // 중복 확인 함수 (아이디, 이메일에 적용할)
  const checkIsAvailable = async (item, checkAPI, setState) => {
    try {
      const result = await checkAPI(userForm[item]);

      if (result) {
        setState(true);
        alert('중복확인이 완료되었습니다!');
      } else {
        setState(false);
        alert(`중복된 ${item} 입니다!`);
      }
    } catch (err) {
      const { errMessage } = err.response.data;
      setState(false);
      alert(errMessage);
    }
  };

  // 아이디 중복 확인
  const handleCheckId = (e) => {
    e.preventDefault();

    checkIsAvailable('id', checkIdAPI, setCheckId);
  };

  // 이메일 중복 확인
  const handleCheckEmail = (e) => {
    e.preventDefault();

    checkIsAvailable('email', checkEmailAPI, setCheckEmail);
  };

  // 닉네임 중복 확인
  const handleCheckNickName = (e) => {
    e.preventDefault();

    checkIsAvailable('nickName', checkNickNameAPI, setCheckNickName);
  };

  return (
    <Wrapper>
      {/* <H2>회원가입</H2> */}
      <Form>
        <InputLabel>아이디</InputLabel>
        <Row>
          <TextInput
            placeholder="ID"
            name="id"
            onChange={handleChangeForm}
            onBlur={handleIdValidation}
          />
          <CheckButton onClick={handleCheckId}>중복확인</CheckButton>
          {idMessage && <ValidationErrMsg>{idMessage}</ValidationErrMsg>}
        </Row>
        {/* {idMessage && <ValidationErrMsg>{idMessage}</ValidationErrMsg>} */}

        <InputLabel>비밀번호</InputLabel>
        <Row>
          <TextInput
            type="password"
            placeholder="password"
            name="password"
            onChange={handleChangeForm}
            onBlur={handlePasswordValidation}
          />
          {passwordMessage && (
            <ValidationErrMsg>{passwordMessage}</ValidationErrMsg>
          )}
        </Row>

        <InputLabel>비밀번호 확인</InputLabel>
        <Row>
          <TextInput
            type="password"
            placeholder="password confirm"
            name="passwordConfirm"
            onChange={handleChangeForm}
            onBlur={handlePasswordConfirmValidation}
          />
          {passwordConfirmMessage && (
            <ValidationErrMsg>{passwordConfirmMessage}</ValidationErrMsg>
          )}
        </Row>

        <InputLabel>이메일</InputLabel>
        <Row style={{ justifyContent: 'space-between' }}>
          <TextInput
            placeholder="email"
            name="email"
            value={userForm.email}
            onChange={handleChangeForm}
            onBlur={handleEmailValidation}
          />
          <CheckButton onClick={handleCheckEmail}>중복확인</CheckButton>
          {emailMessage && <ValidationErrMsg>{emailMessage}</ValidationErrMsg>}
        </Row>
        <InputLabel>이름</InputLabel>
        <Row>
          <TextInput
            placeholder="name"
            name="name"
            value={userForm.name}
            onChange={handleChangeForm}
            onBlur={handlNameValidation}
          />
          {nameMessage && <ValidationErrMsg>{nameMessage}</ValidationErrMsg>}
        </Row>
        <InputLabel>닉네임</InputLabel>
        <Row style={{ justifyContent: 'space-between' }}>
          <TextInput
            placeholder="nickname"
            name="nickName"
            onChange={handleChangeForm}
            onBlur={handlNickNameValidation}
          />
          {/* <CheckButton onClick={handleCheckNickName}>중복확인</CheckButton> */}
          {nickNameMessage && (
            <ValidationErrMsg>{nickNameMessage}</ValidationErrMsg>
          )}
        </Row>
        <SignUpButton onClick={handleClickSignUp}>회원가입</SignUpButton>
      </Form>
    </Wrapper>
  );
}

export default SignUp;
