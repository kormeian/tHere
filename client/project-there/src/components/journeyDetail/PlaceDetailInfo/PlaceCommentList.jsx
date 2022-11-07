import React, { useState, useRef, useEffect } from 'react';
import styled from 'styled-components';
import PlaceComment from './PlaceComment';
import { authAxios } from '../../../lib/utills/customAxios';
import { useAuthValue } from '../../../contexts/auth';

const conditionalChain = (condition, then, otherwise) => (condition ? then : otherwise);

const StyledCommentHolder = styled.ul`
  margin-top: ${(props) => (props.contentOverflowed ? '20px' : '20px')};
  height: ${(props) => (props.displayCommentOverflowed ? '60%' : conditionalChain(props.commentOverflowed, '28%', '35%'))};
  border-bottom: ${(props) => !props.commentOverflowed && '1px solid #bcc4c6'};
  position: ${(props) => (props.displayCommentOverflowed ? 'absolute' : 'relative')};
  background-color: var(--color-gray100);
  top:${(props) => (props.displayCommentOverflowed && '18%')};
  left:${(props) => (props.displayCommentOverflowed && '10px')};
  width: 100%;
  span{
    color: var(--color-gray400);
    font-size: var(--font-micro);
  }
  overflow: ${(props) => (props.displayCommentOverflowed ? 'auto' : 'hidden')};
  display: flex;
  flex-direction: column;
  section{
    position:absolute;
    padding-top: 10px;
    display: flex;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height:100%;
    left:0;
    top:0;
    background-color: var(--color-gray100);
    border: 1px solid var(--color-gray400);
    z-index: 20;
    input{
      margin-top: 10%;
      width: 90%;
      font-size: var(--font-micro);
      height: 2em;
      padding: 0 0.5em;
    }
    button{
      padding: 0.5em 1em !important;
      position: absolute;
      right:10px;
      bottom: 10px;
    }
  }
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
const StyledCommentInputHolder = styled.div`
  position: absolute;
  width: 94%;
  bottom: 0%;
  z-index: 12;
  background-color: var(--color-gray100);
  height: 60px;
  display: flex;
  padding-top: 10px;
  form{
    height:100%;
    width:100%;
    position:relative;
  }
  input{
    height: 80%;
    padding: 0px 30px 0px 10px;
    width: 90%;
    border: 1px solid var(--color-gray400);
  }
  button{
    position:absolute;
    right:0px;
    height: 80%;
  }
`;

export default function PlaceCommentList({ isOverflowed, placeId }) {
  const [isCommentOverflowed, setIsCommentOverflowed] = useState(false);
  const [displayCommentOverflowed, setDisplayCommentOverflowed] = useState(false);
  const [comments, setComments] = useState([]);
  const [tmpCommentId, setTmpCommentId] = useState(-1);
  const commentRef = useRef();
  const userInfo = useAuthValue();
  const commentListRef = useRef(comments);
  const initialCommentListRef = useRef();
  const [deleteTarget, setdeleteTarget] = useState(0);
  const [fixTarget, setFixTarget] = useState(0);
  const [fixValue, setFixValue] = useState('');

  const changeFixInputValue = (e) => {
    setFixValue(e.target.value);
  };

  const fixComfirmButtonClick = (e) => {
    e.stopPropagation();
    const newComments = comments?.map((comment) => {
      if (comment?.commentId === fixTarget) {
        const newComment = { ...comment };
        newComment.text = fixValue;
        return newComment;
      }
      return comment;
    });
    setComments(newComments);
    setFixTarget(0);
  };

  // comment를 서버에 제출해주는 함수
  const addPlaceComment = (request) => {
    const url = 'place/comment';
    authAxios
      .post(url, request)
      .then((res) => {
      })
      .catch((err) => {
        window.alert(`${err}로 인해 제출에 실패하였습니다.`);
      });
  };
  // commment를 서버에서 제거 요청해주는 함수
  const deletePlaceComment = (commentId) => {
    const url = `place/comment?commentId=${commentId}`;
    authAxios
      .delete(url)
      .then((res) => {
      })
      .catch((err) => {
        window.alert(`${err}로 인해 제출에 실패하였습니다.`);
      });
  };
  // comment를 서버에 업데이트 요청해주는 함수
  const updatePlaceComment = (request) => {
    const url = 'place/comment';
    console.log(request);
    authAxios
      .put(url, request)
      .then((res) => {
      })
      .catch((err) => {
        window.alert(`${err}로 인해 제출에 실패하였습니다.`);
      });
  };

  // comment의 추가나 변형이 있었는지 확인해주는 함수
  const checkCommentList = () => {
    const initialList = initialCommentListRef.current;
    const afterList = commentListRef.current;
    const deleteList = [];
    const addList = [];
    const fixList = [];

    // 추가 대상 및 수정 대상들을 탐색
    afterList?.forEach((element) => {
      if (element.commentId < 0) {
        console.log(element);
        const commentResult = {
          placeId: element.placeId,
          text: element.text,
        };
        addList.push(commentResult);
      } else {
        let isfixed = false;
        initialList.forEach((initialElement) => {
          if (initialElement.commentId === element.commentId) {
            if (element.text !== initialElement.text) {
              isfixed = true;
            }
          }
        });
        if (isfixed) {
          const fixedComment = {
            commentId: element.commentId,
            text: element.text,
          };
          fixList.push(fixedComment);
        }
      }
    });

    // 삭제 대상인 댓글들이 있는지 탐색
    initialList?.forEach((element) => {
      let isDelete = true;
      afterList.forEach((afterElement) => {
        if (element.commentId === afterElement.commentId) {
          isDelete = false;
        }
      });
      if (isDelete) {
        deleteList.push(element);
      }
    });

    // 추가 대상이 있을 경우 추가해줌
    if (addList?.length !== 0) {
      Promise.all(addList?.forEach((request) => {
        console.log(request);
        addPlaceComment(request);
      })).catch((err) => console.log(err));
    }
    // 삭제 대상이 있을 경우 삭제해줌
    if (deleteList?.length !== 0) {
      Promise.all(deleteList?.forEach((element) => {
        deletePlaceComment(element.commentId);
      })).catch((err) => console.log(err));
    }

    // 수정 대상이 있을 경우 수정해줌
    if (fixList?.length !== 0) {
      Promise.all(fixList?.forEach((element) => {
        updatePlaceComment(element);
      })).catch((err) => console.log(err));
    }
  };

  const handleCommentClick = () => {
    setDisplayCommentOverflowed((pre) => !pre);
  };

  useEffect(() => {
    // 댓글을 가져옴
    const url = `place/comment?placeId=${placeId}&page=${0}&size=${10}`;
    authAxios.get(url).then(({ data }) => {
      if (data?.content?.length !== 0) {
        console.log(data?.content);
        setComments(data?.content);
        initialCommentListRef.current = data?.content;
      }
    }).catch((err) => {
      console.log(err);
    });

    // 댓글에 변경 사항이 있을 때 이를 갱신해줌
    return checkCommentList;
  }, []);

  // 댓글이 넘쳤을 때 더보기 표시해주기
  useEffect(() => {
    if (commentRef && !displayCommentOverflowed) {
      const overFlowCheck = commentRef.current.scrollHeight > commentRef.current.clientHeight;
      setIsCommentOverflowed(overFlowCheck);
    }
  }, [commentRef, displayCommentOverflowed, comments]);

  const handleCommentInput = (e) => {
    e.preventDefault();
    const { value } = e.target.querySelector('input');
    if (value !== '') {
      const newComment = {
        memberNickName: userInfo?.nickName,
        text: value,
        memberProfileImageUrl: userInfo?.profileImageUrl,
        commentId: tmpCommentId,
        placeId,
      };
      setTmpCommentId((pre) => pre - 1);
      setComments((prev) => [...prev, newComment]);
      e.target.querySelector('input').value = '';
    }
  };

  // Comment가 변할 때마다 이를 commentListRef로 감시해줌
  useEffect(() => {
    commentListRef.current = comments;
  }, [comments]);

  // deleteButton을 눌렀을 때, 관련된 처리를 해줌
  useEffect(() => {
    const newCommentList = comments.filter((comment) => {
      if (comment.commentId === deleteTarget) {
        return false;
      }
      return true;
    });
    if (newCommentList?.length !== comments?.length) {
      setComments(newCommentList);
    }
  }, [deleteTarget]);

  // fixButton을 눌렀을 때, 관련된 처리를 해줌
  useEffect(() => {
    if (fixTarget !== 0) {
      let textValue = '';
      comments?.forEach((comment) => {
        if (comment.commentId === fixTarget) {
          textValue = comment.text;
        }
      });
      setFixValue(textValue);
    }
  }, [fixTarget]);

  return (
    <>
      <StyledCommentHolder
        contentOverflowed={isOverflowed}
        commentOverflowed={isCommentOverflowed}
        displayCommentOverflowed={displayCommentOverflowed}
        ref={commentRef}
      >
        {comments?.length === 0 && (<span>댓글이 없습니다.</span>)}
        {comments?.length !== 0 &&
          (comments.map((comment) =>
            <PlaceComment
              key={comment?.commentId}
              comment={comment}
              controlDelete={setdeleteTarget}
              controlFix={setFixTarget}
            />))}
        {fixTarget !== 0 && (
          <section>
            댓글 수정하기
            <input
              value={fixValue}
              onChange={changeFixInputValue}
            />
            <button
              type="button"
              onClick={fixComfirmButtonClick}
            >
              확인
            </button>
          </section>)}
      </StyledCommentHolder>
      {
        !!isCommentOverflowed && (
          <StyledContentDetail
            onClick={handleCommentClick}
            displayCommentOverflowed={displayCommentOverflowed}
          >
            {!displayCommentOverflowed ? '더보기' : '접기'}
          </StyledContentDetail>
        )
      }
      <StyledCommentInputHolder>
        <form
          onSubmit={handleCommentInput}
        >
          <input type="text" placeholder="댓글 쓰기" />
          <button type="submit">등록</button>
        </form>
      </StyledCommentInputHolder>
    </>
  );
}
