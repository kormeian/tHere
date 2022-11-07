import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAuthActions, useAuthValue } from '../../../contexts/auth';
import { removeAccessToken } from '../../../lib/utills/controlAccessToken';
import { removeRefreshToken } from '../../../lib/utills/controlRefreshToken';

const MyPageList = styled.div`
  position: absolute;
  right: 0;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  background: var(--color-gray100);
  border: 0.5px solid var(--color-green200);
  border-radius: 4px;
  padding: 12px 0 8px;
  z-index: 9999;

  & button {
    color: var(--color-green200);
    font-size: var(--font-micro);
    letter-spacing: -0.0625em;
    padding: 8px 16px;
    border: 0;
    width: 100%;
  }
`;

function MyPagePopOver({ onClose }) {
  const { id } = useAuthValue();
  const { initUserInfo } = useAuthActions();
  const navigate = useNavigate();

  const handleSignOut = () => {
    removeAccessToken();
    removeRefreshToken();
    initUserInfo();
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <MyPageList onClick={handleClose}>
      <Link to="/journey/upload">
        <button type="button">여정 업로드</button>
      </Link>
      <Link to={`/myjourney/${id}`}>
        <button type="button">나의 여정 목록</button>
      </Link>
      <Link to={`/bookmark/${id}`}>
        <button type="button">저장한 여정 목록</button>
      </Link>
      <Link to={`/profile/${id}`}>
        <button type="button">프로필 수정</button>
      </Link>
      <Link to="/">
        <button type="button" onClick={handleSignOut}>
          로그아웃
        </button>
      </Link>
    </MyPageList>
  );
}

export default MyPagePopOver;
