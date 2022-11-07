import React, { useState } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Cookies } from 'react-cookie';
import { FaGoogle } from 'react-icons/fa';
import { setRefreshToken } from '../../lib/utills/controlRefreshToken';
import { setAccessToken } from '../../lib/utills/controlAccessToken';
import { signinAPI } from '../../lib/utills/http';

const Wrapper = styled.div`
  display: flex;
  justify-content: center;
`;

const Form = styled.form`
  padding-top: 220px;
  font-size: var(--font-small);

  width: 280px;
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

const SignInButton = styled.button`
  display: block;
  margin: 16px auto;
  padding: 8px 24px;
  font-size: var(--font-small);
  background-color: var(--color-green200);
  color: var(--color-gray100);
  border: none;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
`;

const Row = styled.div`
  position: relative;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 32px;
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

const SignUpLink = styled.div`
  width: 100%;
  margin: 0 auto;
  display: flex;
  gap: 6px;
  justify-content: flex-end;
  color: var(--color-gray500);
  font-size: var(--font-micro);
  margin-top: -16px;
  margin-bottom: 60px;

  & strong {
    font-weight: var(--weight-semi-bold);
  }
`;

function SignIn() {
  // 폼데이터 관리용 상태
  const [loginForm, setLoginForm] = useState({
    id: '',
    password: '',
  });

  const handleClickLogin = async (e) => {
    e.preventDefault();

    console.log(loginForm);

    try {
      const {
        accessToken,
        grantType,
        refreshToken,
        refreshTokenExpirationTime,
      } = await signinAPI(loginForm);

      setRefreshToken(refreshToken, refreshTokenExpirationTime);
      setAccessToken(accessToken);

      // 수정 필요해보임
      window.location.replace('/');
    } catch (err) {
      const { errCode, errMessage } = err.response.data;
      alert(errMessage);
    }
  };

  // 폼데이터 상태 관리
  const handleChangeForm = ({ target }) => {
    setLoginForm((prev) => ({
      ...prev,
      [target.name]: target.value,
    }));
  };

  return (
    <Wrapper>
      <H2>로그인</H2>
      <Form>
        <InputLabel>아이디</InputLabel>
        <Row>
          <TextInput
            placeholder="아이디"
            name="id"
            onChange={handleChangeForm}
          />
        </Row>
        <InputLabel>비밀번호</InputLabel>
        <Row>
          <TextInput
            type="password"
            placeholder="비밀번호"
            name="password"
            onChange={handleChangeForm}
          />
        </Row>
        <SignUpLink>
          <span>회원이 아니신가요? </span>
          <Link to="/signup">
            <strong>회원가입</strong>
          </Link>
        </SignUpLink>

        <SignInButton type="button" onClick={handleClickLogin}>
          로그인
        </SignInButton>
        <a href="http://ec2-3-34-2-239.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/google">
          <SignInButton type="button">
            <FaGoogle />
          </SignInButton>
        </a>
      </Form>
    </Wrapper>
  );
}

export default SignIn;
