import React, { useEffect } from 'react';
import JourneyUploadContainer from '../components/journeyUpload/JourneyUploadContainer';
import { useAuthActions } from '../contexts/auth';
import NewJourneyProvider from '../contexts/newJourney';
import { getAccessToken } from '../lib/utills/controlAccessToken';

function JourneyUploadPage() {
  // 유저 인증
  const { authenticateUser, initUserInfo } = useAuthActions();

  useEffect(() => {
    // 마운트 될 때 accessToken으로 받은 유저 정보 업데이트
    const accessToken = getAccessToken();
    if (accessToken) {
      console.log(accessToken);
      authenticateUser(accessToken);
    }
  }, []);

  return (
    <NewJourneyProvider>
      <JourneyUploadContainer />
    </NewJourneyProvider>
  );
}

export default JourneyUploadPage;
