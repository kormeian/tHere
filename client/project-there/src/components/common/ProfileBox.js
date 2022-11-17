import React from 'react';
import styled from 'styled-components';
import { useJourneyListValue } from '../../contexts/JourneyListContext';

const Wrapper = styled.div`
  margin-bottom: 80px;
  width: 984px;
  padding: 22px 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 18px;
  color: var(--color-green300);
  font-size: var(--font-small);
  font-weight: var(--weight-semi-bold);
  border-bottom: 0.5px solid var(--color-gray300);
`;

const ProfileImageBox = styled.div`
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--color-gray400);

  & img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

function ProfileBox() {
  const [journeyList] = useJourneyListValue();

  return (
    <Wrapper>
      <ProfileImageBox>
        <img src={journeyList[0]?.profileImageUrl || ''} alt="" />
      </ProfileImageBox>
      <div>{journeyList[0]?.nickName || ''}</div>
    </Wrapper>
  );
}

export default ProfileBox;
