import React, { useState } from 'react';
import styled from 'styled-components';
import { BsBookmark, BsBookmarkFill } from 'react-icons/bs';
import { useJourneyDetailValue } from '../../../contexts/journeyDetail';
import { useAuthValue } from '../../../contexts/auth';
import { authAxios } from '../../../lib/utills/customAxios';

const Container = styled.div`
  position: absolute;
  top: 12px;
  right: 0;
`;

const Button = styled.button`
  && {
    border: 0;
    background: none;
    padding: 0;
  }
`;

const BookmarkIcon = styled(BsBookmark)`
  font-size: 24px;
`;

const BookmarkFillIcon = styled(BsBookmarkFill)`
  font-size: 24px;
`;

function Bookmark({ journeyId }) {
  const { id } = useAuthValue();

  const [isBookmarked, setIsBookmarked] = useState(false);

  const addBookmark = async () => {
    try {
      const requestBody = {
        memberId: id,
        journeyId,
      };

      const { status } = await authAxios.post('/bookmark', requestBody);

      return status;
    } catch (err) {
      console.log(err);
    }
  };

  // API 명세서 업데이트 되면 수정해야 함
  const removeBookmark = async () => {
    try {
      const requestBody = {
        memberId: id,
        journeyId,
      };

      const { status } = await authAxios.delete('/bookmark', requestBody);

      return status;
    } catch (err) {
      console.log(err);
    }
  };

  const handleAddBookmark = async () => {
    const status = await addBookmark();
    if (status === 200) setIsBookmarked(true);
  };

  const handleRemoveBookmark = async () => {
    const status = await removeBookmark();
    if (status === 200) setIsBookmarked(false);
  };

  return (
    <Container>
      {isBookmarked ? (
        <Button type="button" onClick={handleRemoveBookmark}>
          <BookmarkFillIcon />
        </Button>
      ) : (
        <Button type="button" onClick={handleAddBookmark}>
          <BookmarkIcon />
        </Button>
      )}
    </Container>
  );
}

export default Bookmark;
