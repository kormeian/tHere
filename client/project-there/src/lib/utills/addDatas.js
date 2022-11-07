import axios from 'axios';
import { getAccessToken } from './controlAccessToken';

/**
 * axios 데이터 전송 함수
 * @param {object} obj
 * @param {string} url
 */
const addDatas = (datas, url) => {
  // formData 객체를 통해 데이터 전달
  const formData = new FormData();
  const value = { ...datas };

  if (value.thumbnail) {
    formData.append('thumbnail', value.thumbnail);
  }
  delete value.thumbnail;

  const blob = new Blob([JSON.stringify(value)], { type: 'application/json' });
  formData.append('request', blob);

  const config = {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  };

  // // 토큰 유무 확인
  // const accessToken = getAccessToken();
  // if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;

  axios
    // url로 formData를 config에 맞게 전송
    .post(url, formData, config)
    .then(({ data }) => {
      console.log(data);
      alert('등록 성공');

      return data.journeyId;
    })
    .catch((err) => {
      const { errorCode, errorMessage } = err.response.data;
      console.log(errorCode);
      console.log(errorMessage);
      alert(`등록 실패 : ${err}`);
    });
};

export default addDatas;
