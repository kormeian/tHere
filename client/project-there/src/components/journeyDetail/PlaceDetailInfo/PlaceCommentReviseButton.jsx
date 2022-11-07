import React, { useState } from 'react';
import styled from 'styled-components';

const ButtonHolder = styled.div`
  position: absolute;
  right: -5%;
  top:0;
  z-index: 12;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  button{
    padding: 0.5em 0.25em;
  }
  .textHolder{
    font-size: 20px;
    color:black;
    border: none;
    padding: none;
    position: relative;
    top:-5px;
    background-color: var(--color-gray100);
  }
  .wrapper{
    border: 1px solid var(--color-green100);
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 20px;
    background-color: rgba(255,255,255,0.9);
  }
  .fixButton{
    color:blue;
    background: none;
    border:none;
  }
  .deleteButton{
    color: red;
    background: none;
    border:none;
  }
`;

export default function PlaceCommentReviseButton({ controlDelete, controlFix,
  commentId }) {
  const [isOpen, setIsOpen] = useState(false);

  const handleReviseButtonCilck = () => {
    setIsOpen((pre) => !pre);
  };

  const handleDelete = (e) => {
    e.stopPropagation();
    controlDelete(commentId);
    setIsOpen(false);
  };

  const handleFix = (e) => {
    e.stopPropagation();
    console.log('fix click');
    controlFix(commentId);
    setIsOpen(false);
  };

  return (
    <ButtonHolder>
      {isOpen && (
        <div
          className="wrapper"
        >
          <button
            type="button"
            className="fixButton"
            onClick={handleFix}
          >
            수정
          </button>
          <button
            type="button"
            className="deleteButton"
            onClick={handleDelete}
          >
            삭제
          </button>
        </div>
      )}
      <button
        type="button"
        className="textHolder"
        onClick={handleReviseButtonCilck}
      >
        ...
      </button>
    </ButtonHolder>
  );
}
