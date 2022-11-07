import React, { useCallback, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { setRefreshToken } from '../../lib/utills/controlRefreshToken';
import { setAccessToken } from '../../lib/utills/controlAccessToken';

function Oauth2Redirect() {
  const navigate = useNavigate();

  const getParams = useCallback(() => {
    console.log('Oauth2Redirect');

    const params = new URLSearchParams(window.location.search);

    const isNewMember = params.get('newMember');
    if (isNewMember === 'true') {
      const name = params.get('name');
      const email = params.get('email');

      navigate('/signup', { state: { name, email } });
    } else if (isNewMember === 'false') {
      const accessToken = params.get('accessToken');
      const refreshToken = params.get('refreshToken');
      const expirationTime = params.get('expirationTime');
      setRefreshToken(refreshToken, expirationTime);
      setAccessToken(accessToken);

      window.location.replace('/');
    }
  }, []);

  useEffect(() => {
    getParams();
    // console.log('Oauth2Redirect');
    // const params = new URLSearchParams(window.location.search);
    // const accessToken = params.get('accessToken');
    // const refreshToken = params.get('refreshToken');
    // const expirationTime = params.get('expirationTime');
    // const name = params.get('name');
    // const email = params.get('email');
    // const isNewMember = params.get('newMember');
    // console.log(`isNewMember ::: ${isNewMember}`);
    // setRefreshToken(refreshToken, expirationTime);
    // setAccessToken(accessToken);
    // // 새로운 유저면 추가 정보 입력 페이지로 redirection
    // if (isNewMember === 'true') {
    //   alert('new member');
    //   // window.location.replace('/signup');
    //   navigate('/signup', { state: { name, email } });
    // } else if (isNewMember === 'false') {
    //   // alert('not new member');
    //   // window.location.replace('/');
    // }
  }, []);

  // return <><div>a</div></>;
}

export default Oauth2Redirect;
