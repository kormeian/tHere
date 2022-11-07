import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAuthValue } from '../../contexts/auth';
import { checkNickNameAPI, updateUserInfoAPI } from '../../lib/utills/http';
import SignUp from '../signUp/SignUp';
import ImageUploader from './ImageUploader';

const Wrapper = styled.div`
  display: flex;
  gap: 96px;
  justify-content: center;
  padding-top: 160px;
  font-size: var(--font-small);

  position: relative;
`;

const Form = styled.form`
  width: 330px;
  overflow: hidden;
  padding-bottom: 42px;

  position: relative;
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

const ProfileEditButton = styled.button`
  display: block;
  margin: 24px auto;
  padding: 8px 24px;
  font-size: var(--font-small);
  background-color: var(--color-green200);
  color: var(--color-gray100);
  border: none;

  position: absolute;
  right: 0;
  bottom: -21px;
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
  padding: 8px 16px;
  position: absolute;
  right: 0;
  color: 0.5px solid var(--color-green200);
  border: 0.5px solid var(--color-green200);
`;

const ValidationErrMsg = styled.div`
  color: red;
  position: absolute;
  bottom: -21px;
  left: 0;
  font-size: var(--font-micro);
`;

const PasswordChange = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48px;

  & * {
    display: block;
    position: static;
    margin: 0;
    width: inherit;
  }
`;

const validationErrorMessage = {
  password:
    '영문/숫자/특수문자, 1개 이상의 대문자, 소문자, 숫자, 특수문자 포함 (10~20자)',
  passwordConfirm: '비밀번호가 일치하지 않습니다.',
  nickName: '한글/영문/숫자 (2~10자)',
};

// 미입력 값 확인 메세지
const emptyValueErrorMessage = {
  nickName: '닉네임이 입력되지 않았습니다.',
  password: '비밀번호가 입력되지 않았습니다.',
  passwordConfirm: '비밀번호 확인이 입력되지 않았습니다.',
};

function ProfileEdit({ editMode }) {
  console.log(editMode);

  // 프로필 수정 완료 시 메인 페이지로 이동 위함
  const navigate = useNavigate();

  // 기존 회원 정보
  const userInfo = useAuthValue();
  // console.log(userInfo);

  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [nickNameMessage, setNickNameMessage] = useState('');

  const [checkNickName, setCheckNickName] = useState(false);

  // 비밀번호 변경 모드 on/off
  const [passwordEditMode, setPasswordEditMode] = useState(false);

  // 폼 데이터 상태 관리
  const [userForm, setUserForm] = useState({
    id: '',
    password: '',
    passwordConfirm: '',
    email: '',
    name: '',
    nickName: '',
    profileImageUrl: null,
  });

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

  const handlePasswordEditMode = () => {
    setPasswordEditMode(!passwordEditMode);
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

  // 닉네임 유효성 검사
  const handleNickNameValidation = ({ target }) => {
    const nickNameRegex = /^[0-9a-zA-Z가-힣]{2,10}$/;
    checkValidation(target, nickNameRegex, setNickNameMessage);
  };

  // // 닉네임 중복 확인
  // const handleCheckNickName = async (e) => {
  //   e.preventDefault();

  //   try {
  //     const res = await checkNickNameAPI(userForm.nickName);

  //     if (res) {
  //       setCheckNickName(true);
  //       alert('중복확인이 완료되었습니다!');
  //     } else {
  //       setCheckNickName(false);
  //       alert('중복된 닉네임입니다!');
  //     }
  //   } catch (err) {
  //     const { errMessage } = err.response.data;
  //     setCheckNickName(false);
  //     alert(errMessage);
  //   }
  // };

  const handleModifyProfile = async (e) => {
    e.preventDefault();

    console.log(userForm);

    // 비밀번호 미입력 에러 메세지
    if (passwordEditMode) {
      if (!userForm.password) {
        return alert(emptyValueErrorMessage.password);
      }

      if (!userForm.passwordConfirm) {
        return alert(emptyValueErrorMessage.passwordConfirm);
      }
    }

    // 닉네임 미입력 에러 메세지
    if (!userForm.nickName) {
      return alert(emptyValueErrorMessage.nickName);
    }

    // 닉네임 유효성 검사 에러 메세지
    if (nickNameMessage) {
      return alert('잘못된 닉네임 형식입니다.');
    }

    // // 닉네임 중복 확인 에러 메세지
    // if (!checkNickName) {
    //   return alert('닉네임 중복 확인을 완료해주세요');
    // }

    try {
      const res = await updateUserInfoAPI(userForm);
      console.log(res);

      alert('수정이 완료되었습니다.');
      navigate('/');
    } catch (err) {
      const { errCode, errMessage } = err.response.data;
      alert(errMessage);
      setCheckNickName(false);
    }
  };

  useEffect(() => {
    // 이전 정보 렌더링
    Object.entries(userInfo).forEach(([key, value]) => {
      if (value) {
        setUserForm((prev) => ({ ...prev, [key]: value }));
      }
    });
  }, []);

  return (
    <Wrapper>
      {/* <H2>프로필 수정</H2> */}
      <div>
        {/* <InputLabel>프로필 이미지</InputLabel> */}
        <ImageUploader
          onChangeForm={setUserForm}
          imgUrl={userForm.profileImageUrl || ''}
        />
      </div>

      <Form>
        <InputLabel>아이디</InputLabel>
        <Row>
          <TextInput readOnly name="id" value={userForm.id} />
        </Row>
        {passwordEditMode ? (
          <>
            <InputLabel>비밀번호</InputLabel>
            <Row>
              <TextInput
                type="password"
                placeholder="비밀번호"
                name="password"
                onChange={handleChangeForm}
                onBlur={handlePasswordValidation}
              />
              <CheckButton type="button" onClick={handlePasswordEditMode}>
                취소
              </CheckButton>
              {passwordMessage && (
                <ValidationErrMsg>{passwordMessage}</ValidationErrMsg>
              )}
            </Row>
            <InputLabel>비밀번호 확인</InputLabel>
            <Row>
              <TextInput
                type="password"
                placeholder="비밀번호 확인"
                name="passwordConfirm"
                onChange={handleChangeForm}
                onBlur={handlePasswordConfirmValidation}
              />
              {passwordConfirmMessage && (
                <ValidationErrMsg>{passwordConfirmMessage}</ValidationErrMsg>
              )}
            </Row>
          </>
        ) : (
          <PasswordChange>
            <InputLabel>비밀번호</InputLabel>
            <Row>
              <CheckButton type="button" onClick={handlePasswordEditMode}>
                변경
              </CheckButton>
            </Row>
          </PasswordChange>
        )}

        <InputLabel>이메일</InputLabel>
        <Row>
          <TextInput readOnly name="email" value={userForm.email} />
        </Row>
        <InputLabel>이름</InputLabel>
        <Row>
          <TextInput readOnly name="name" value={userForm.name} />
        </Row>
        <InputLabel>닉네임</InputLabel>
        <Row>
          <TextInput
            placeholder="닉네임"
            name="nickName"
            onChange={handleChangeForm}
            onBlur={handleNickNameValidation}
          />
          {/* <CheckButton onClick={handleCheckNickName}>중복확인</CheckButton> */}
          {nickNameMessage && (
            <ValidationErrMsg>{nickNameMessage}</ValidationErrMsg>
          )}
        </Row>
        <ProfileEditButton onClick={handleModifyProfile}>
          수정
        </ProfileEditButton>
      </Form>
    </Wrapper>
  );
}

export default ProfileEdit;
