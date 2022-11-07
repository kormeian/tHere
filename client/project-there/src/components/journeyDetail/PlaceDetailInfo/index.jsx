import React, { useRef, useEffect, useState } from 'react';
import styled from 'styled-components';
import { AiOutlineHeart, AiFillHeart } from 'react-icons/ai';
import { authAxios } from '../../../lib/utills/customAxios';
import { useAuthValue } from '../../../contexts/auth';
import PlaceCommentList from './PlaceCommentList';
import CommentList from '../../../datas/comment';

const contentHeight = (condition, then, otherwise) => (condition ? then : otherwise);

const StyledInfoHolder = styled.div`
  width: 50%;
  height: 100%;
  padding: 2%;
  overflow: hidden;
  position: relative;
`;
const StyledTitle = styled.h2`
  font-size: 24px !important;
  font-weight: 300;
  letter-spacing: -4%;
  margin-bottom: 12%;
`;
const StyeldContents = styled.p`
  font-weight: 300 !important;
  font-size: 14px !important;
  letter-spacing: -4% !important;
  height: ${(props) => (props.displayOverFlowed ? '58%' : contentHeight(props.overflowed, '20%', '25%'))};
  white-space: pre-wrap;
  text-overflow: ${(props) => !props.displayOverFlowed && 'ellipsis'};
  overflow: ${(props) => (props.displayOverFlowed ? 'auto' : 'hidden')};
  word-break: break-all;
  display: ${(props) => !props.displayOverFlowed && '-webkit-box'};
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  border-bottom: ${(props) => !props.overflowed && '1px solid #bcc4c6'};
`;
const StyledContentDetail = styled.div`
  color: #bcc4c6;
  font-size: 12px;
  font-weight: 500;
  width: 100%;
  position: ${(props) => (props.displayCommentOverflowed ? 'absolute' : 'relative')};
  top:${(props) => (!props.displayCommentOverflowed && '10px')};
  bottom:${(props) => (props.displayCommentOverflowed && '14%')};
  cursor: pointer;
  background-color: var(--color-gray100);
  height:20px;
  border-bottom: 1px solid var(--color-gray400);
`;

const StyledLikeHolder = styled.div`
  padding: 0 0 5px 0;
  display: flex;
  align-items: center;
  span{
    margin-left: 6px;
    font-size: var(--font-regular);
    height:25px;
    font-weight: var(--weight-bold);
    padding-top: 2px;
  }
  margin-bottom: 10px;
`;
const StyledLikeIconHolder = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  svg{
    width: 20px;
    height: 20px;
    color: red;
  }
`;

export default function PlaceDetailInfo({ target }) {
  const [isOverflowed, setIsOverFlowed] = useState(false);
  const [displayOverflowed, setDisplayOverFlowed] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [isLiked, setIsLiked] = useState(false);
  const memberInfo = useAuthValue();
  const textRef = useRef();
  const likeRef = useRef(isLiked);

  // 좋아요 표시한 부분에 변경이 있었는지 확인해주는 함수
  const checkLiked = () => {
    if (likeRef.current !== target.heartedCheck) {
      if (likeRef.current) {
        const url = `place/heart?placeId=${target.placeId}&memberId=${memberInfo.id}`;
        authAxios.post(url).catch((err) => { console.log(err); });
      } else {
        const url = `place/unheart?placeId=${target.placeId}&memberId=${memberInfo.id}`;
        authAxios.post(url).catch((err) => { console.log(err); });
      }
    }
  };

  useEffect(() => {
    if (textRef && !displayOverflowed) {
      const overFlowCheck = textRef.current.scrollHeight > textRef.current.clientHeight;
      setIsOverFlowed(overFlowCheck);
    }
  }, [textRef, displayOverflowed]);

  // 좋아요 데이터 받아오기
  useEffect(() => {
    // 좋아요 수를 반영해줌
    const { placeHeartCount } = target;
    if (placeHeartCount.indexOf('k') === -1) {
      setLikeCount(Number(placeHeartCount));
    } else {
      setLikeCount(placeHeartCount);
    }
    // 좋아요 유무를 반영해줌
    if (target.heartedCheck) {
      setIsLiked(true);
    }
    return checkLiked;
  }, []);

  // 마지막에 좋아요 바뀌었는지 유무를 보내주기 위해 ref 업데이트를 해주는 부분
  useEffect(() => {
    likeRef.current = isLiked;
  }, [isLiked]);

  const handleContentClick = () => {
    setDisplayOverFlowed((pre) => !pre);
  };
  const handleLikeButtonClick = () => {
    setIsLiked((pre) => !pre);
    if (typeof (likeCount) === 'number') {
      if (isLiked) {
        setLikeCount((pre) => pre - 1);
      } else {
        setLikeCount((pre) => pre + 1);
      }
    }
  };

  return (
    <StyledInfoHolder>
      <StyledLikeHolder>
        <StyledLikeIconHolder
          onClick={handleLikeButtonClick}
        >
          {isLiked ? <AiFillHeart /> : <AiOutlineHeart />}
        </StyledLikeIconHolder>
        <span>
          {likeCount}
        </span>
      </StyledLikeHolder>
      <StyledTitle>{target.title}</StyledTitle>
      <StyeldContents
        ref={textRef}
        displayOverFlowed={displayOverflowed}
        overflowed={isOverflowed}
      >
        {target.text}
      </StyeldContents>
      {!!isOverflowed && (
        <StyledContentDetail
          onClick={handleContentClick}
        >
          {!displayOverflowed ? '더보기' : '접기'}
        </StyledContentDetail>
      )}
      <PlaceCommentList
        isOverflowed={isOverflowed}
        placeId={target?.placeId}
      />
    </StyledInfoHolder>
  );
}
