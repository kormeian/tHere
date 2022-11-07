import axios from 'axios';
import React, { useCallback, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAuthValue } from '../../../contexts/auth';
import {
  useJourneyDetailActions,
  useJourneyDetailValue,
} from '../../../contexts/journeyDetail';
import { useNewJourneyActions } from '../../../contexts/newJourney';
import { authAxios } from '../../../lib/utills/customAxios';

const BtnContainer = styled.div`
  position: absolute;
  top: 140px;
  right: 98px;
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  border: 1px solid black;
  border-radius: var(--size-border-radius-small);
  overflow: hidden;

  & button {
    border: 0;
    border-radius: 0;
    background: var(--color-gray100);
    padding: 8px 24px;
    margin: 0;

    &:first-child {
      border-bottom: 1px solid var(--color-green200);
    }

    &:hover {
      background: var(--color-gray200);
    }
  }
`;

function ViewMorePopOver({ journeyId }) {
  const journey = useJourneyDetailValue();
  const { id } = useAuthValue();

  const navigate = useNavigate();

  const deleteDatas = useCallback(() => {
    authAxios
      .delete(`/journey?journeyId=${journeyId}`)
      .then((res) => {
        console.log(res);
        alert('삭제가 완료되었습니다.');
      })
      .catch((err) => console.log(err));
  });

  const handleEditBtnClick = () => {
    if (id !== journey.memberId) {
      return alert('수정 권한이 없습니다.');
    }

    navigate(`/journey/update/${journeyId}`);
  };

  const handleDeleteBtnClick = () => {
    if (id !== journey.memberId) {
      return alert('삭제 권한이 없습니다.');
    }

    deleteDatas();
    navigate('/');
  };

  return (
    <BtnContainer>
      <button type="button" onClick={handleEditBtnClick}>
        수정
      </button>
      <button type="button" onClick={handleDeleteBtnClick}>
        삭제
      </button>
    </BtnContainer>
  );
}

export default ViewMorePopOver;
