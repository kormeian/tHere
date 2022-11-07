import axios from 'axios';
import { SERVER_BASE_URL } from '../constants/serverBaseUrl';
import {
  getAccessToken,
  removeAccessToken,
  setAccessToken,
} from './controlAccessToken';
import {
  getRefreshToken,
  removeRefreshToken,
  setRefreshToken,
} from './controlRefreshToken';

const http = axios.create({
  baseURL: SERVER_BASE_URL,
});

// interceptors 때문에 axios 버전 낮춰야 함
http.interceptors.response.use(
  (response) => response,
  (error) => {
    // res에서 err가 발생했을 경우 catch로 넘어가기 전에 처리하는 부분
    const { errorCode } = error.response.data;
    const originalRequest = error.config;

    if (errorCode === 'EXPIRED_ACCESS_TOKEN' && !originalRequest.retry) {
      originalRequest.retry = true;
      const prevRefreshToken = getRefreshToken();
      const prevAccessToken = getAccessToken();

      return http
        .post('/member/reissue', {
          prevAccessToken,
          prevRefreshToken,
        })
        .then((res) => {
          const { accessToken, refreshToken } = res.data;
          setAccessToken(accessToken);
          setRefreshToken(refreshToken);
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          console.log(originalRequest);

          return axios(originalRequest);
        })
        .catch((err) => {
          console.log('토큰 재발행 에러');
          console.log(err);
          removeRefreshToken();
          removeAccessToken();
          return false;
        });
    }

    return Promise.reject(error);
  },
);

// 반환값 로그인한 회원 이름
export const authAPI = async (accessToken) => {
  try {
    const response = await http.get('/members/auth', {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    return response.data;
  } catch (e) {
    const { errorCode, errorMessage } = e.response.data;
    console.log(errorCode);
    console.log(errorMessage);

    if (errorCode === 'INVALID_ACCESS_TOKEN') {
      alert('다시 로그인 해주세요!');
    }
    // window.location.replace('/signin');
  }
};

// 반환값 boolean
// true: 사용 가능한 이메일, false: 사용 불가능한 이메일
export const checkEmailAPI = async (email) => {
  try {
    const response = await http.post('/members/check/email', { email });
    return response.data.result;
  } catch (e) {
    console.log(e);
    throw e;
  }
};

// 반환값 boolean
// true: 사용 가능한 아이디, false: 사용 불가능한 아이디
export const checkIdAPI = async (id) => {
  try {
    const response = await http.post('/members/check/id', { id });
    return response.data.result;
  } catch (e) {
    console.log(e);
    throw e;
  }
};

// 반환값 boolean
// true: 사용 가능한 아이디, false: 사용 불가능한 아이디
export const checkNickNameAPI = async (nickName) => {
  try {
    const response = await http.post('/members/check/nickName', { nickName });
    return response.data.result;
  } catch (e) {
    console.log(e);
    throw e;
  }
};

// 반환값 메세지, 이메일
export const signupAPI = async (userForm) => {
  try {
    console.log(userForm);

    const value = { ...userForm };

    delete value.profileImage;
    delete value.passwordConfirm;

    console.log('signup API');
    console.log(value);

    const response = await http.post('/members/signup', value);
    return response.data;
  } catch (e) {
    console.log(e);
    throw e;
  }
};

// 회원 정보 수정
export const updateUserInfoAPI = async (userForm) => {
  try {
    console.log(userForm);

    const value = { ...userForm };
    // 비밀번호 확인 프로퍼티 제거
    delete value.passwordConfirm;
    // 프로필 이미지 URL 제거
    delete value.profileImageUrl;

    console.log('회원정보 수정 API');
    console.log(userForm);

    const formData = new FormData();

    // 프로필 이미지 파일이 존재하면 FormData에 추가
    if (value.profileImageFile) {
      formData.append('multipartFile', value.profileImageFile);
    }
    delete value.profileImageFile;

    const blob = new Blob([JSON.stringify(value)], {
      type: 'application/json',
    });
    formData.append('updateRequest', blob);

    const config = {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    };

    const response = await http.post('/members', formData, config);

    return response.data;
  } catch (e) {
    console.log(e);
    throw e;
  }
};

// 반환값 accessToken, grantType, refreshToken, refreshTokenExpirationTime
export const signinAPI = async (loginForm) => {
  try {
    const response = await http.post('/members/signin', {
      ...loginForm,
    });

    // 토큰
    return response.data;
  } catch (e) {
    console.log(e);
    alert('아이디 또는 비밀번호를 다시 확인해주세요.');
  }
};
