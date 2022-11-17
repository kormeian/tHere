import {
  getAccessToken,
  removeAccessToken,
} from '../utills/controlAccessToken';
import {
  getRefreshToken,
  removeRefreshToken,
} from '../utills/controlRefreshToken';
import customAxios from './core/instance';

// 반환값 : 로그인한 회원 이름
export const authAPI = async (accessToken) => {
  if (!accessToken || accessToken === 'undefined') return;

  try {
    const { data } = await customAxios.get('/members/auth');

    return data;
  } catch (err) {
    const { errorCode, errorMessage } = err.response.data;
    if (errorCode === 'INVALID_ACCESS_TOKEN') {
      alert('다시 로그인 해주세요!');
    }
  }
};

// 반환값 : boolean
export const checkEmailAPI = async (email) => {
  try {
    const response = await customAxios.post('/members/check/email', { email });
    return response.data.result;
  } catch (err) {
    console.log(err);
  }
};

// 반환값 boolean
export const checkIdAPI = async (id) => {
  try {
    const response = await customAxios.post('/members/check/id', { id });
    return response.data.result;
  } catch (err) {
    console.log(err);
  }
};

// 반환값 : boolean
export const checkNickNameAPI = async (nickName) => {
  try {
    const response = await customAxios.get(`/members/check/${nickName}`);
    return response.data.result;
  } catch (err) {
    console.log(err);
  }
};

// 반환값 : 메세지, 이메일
export const signupAPI = async (userForm) => {
  try {
    console.log(userForm);

    const value = { ...userForm };

    delete value.profileImage;
    delete value.passwordConfirm;

    console.log('signup API');
    console.log(value);

    const response = await customAxios.post('/members/signup', value);
    return response.data;
  } catch (err) {
    console.log(err);
  }
};

// 회원 정보 수정
export const updateUserInfoAPI = async (userForm) => {
  try {
    const value = { ...userForm };
    // 비밀번호 확인 프로퍼티 제거
    delete value.passwordConfirm;
    // 프로필 이미지 URL 제거
    if (value.profileImageFile) {
      delete value.profileImageUrl;
    }

    const formData = new FormData();

    // 프로필 이미지 추가
    formData.append('multipartFile', value.profileImageFile || null);
    delete value.profileImageFile;

    const blob = new Blob([JSON.stringify(value)], {
      type: 'application/json',
    });
    formData.append('updateRequest', blob);

    const config = {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${getAccessToken()}`,
      },
    };

    const response = await customAxios.patch('/members', formData, config);

    return response.data;
  } catch (err) {
    console.log(err.response.data);
  }
};

// 반환값 accessToken, grantType, refreshToken, refreshTokenExpirationTime
export const signinAPI = async (loginForm) => {
  try {
    const response = await customAxios.post('/members/signin', {
      ...loginForm,
    });
    return response.data;
  } catch (err) {
    console.log(err);
    alert('아이디 또는 비밀번호를 다시 확인해주세요.');
  }
};

// 반환값 없음
export const signoutAPI = async () => {
  const requestBody = {
    accessToken: getAccessToken(),
    refreshToken: getRefreshToken(),
  };

  try {
    await customAxios.post('/members/signout', requestBody);
    removeAccessToken();
    removeRefreshToken();
  } catch (err) {
    console.log(err.response.data);
  }
};
