import axios from 'axios';
import { SERVER_BASE_URL } from '../../constants/serverBaseUrl';
import {
  getAccessToken,
  removeAccessToken,
  setAccessToken,
} from '../../utills/controlAccessToken';
import {
  getRefreshToken,
  removeRefreshToken,
  setRefreshToken,
} from '../../utills/controlRefreshToken';

const customAxios = axios.create({
  baseURL: SERVER_BASE_URL,
});

customAxios.interceptors.request.use((config) => {
  const nextConfig = config;
  const accessToken = getAccessToken();
  if (accessToken === null || accessToken === 'undefined') {
    nextConfig.headers.Authorization = null;
    return nextConfig;
  }
  nextConfig.headers.Authorization = `Bearer ${accessToken}`;
  return nextConfig;
});

customAxios.interceptors.response.use(
  (response) => response,
  async (error) => {
    // res에서 err가 발생했을 경우 catch로 넘어가기 전에 처리하는 부분
    const { errorCode } = error.response.data;
    const originalRequest = error.config;
    if (
      error.response &&
      errorCode === 'EXPIRED_ACCESS_TOKEN' &&
      !originalRequest.retry
    ) {
      try {
        const refreshToken = await getRefreshToken();
        const accessToken = await getAccessToken();
        const res = await customAxios.post('/member/reissue', {
          accessToken,
          refreshToken,
        });
        const { newAccessToken, newRefreshToken } = res.data;
        setAccessToken(newAccessToken);
        setRefreshToken(newRefreshToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;

        // return axios(originalRequest);
        return customAxios.request(originalRequest);
      } catch (err) {
        console.log('토큰 재발행 에러');
        console.log(err);

        // 로그아웃 customAxios로 대체하기!
        // removeRefreshToken();
        // removeAccessToken();

        const requestBody = {
          accessToken: getAccessToken(),
          refreshToken: getRefreshToken(),
        };

        customAxios.post('/members/signout', requestBody).then((res) => {
          removeAccessToken();
          removeRefreshToken();
        });
      }
      return Promise.reject(error);
    }
    return Promise.reject(error);
  },
);

export default customAxios;
