import React, { useEffect, useMemo, useState } from 'react';
import styled from 'styled-components';
import {
  useJourneyDetailActions,
  useJourneyDetailValue,
} from '../../../contexts/journeyDetail';

import NewJourneyProvider, {
  useNewJourneyValue,
  useNewJourneyActions,
} from '../../../contexts/newJourney';
import colors from '../../../lib/constants/colors';
import ContentArea from './ContentArea';
import TitleArea from './TitleArea';
import ViewMorePopOver from './ViewMorePopOver';

const { gray100, green300 } = colors;

const Container = styled.div`
  position: relative;
  width: 100%;
  padding: 10vh 5vw;
  background: ${gray100};
  color: ${green300};
  height: 100vh;
  border-radius: 30px;

  & button {
    color: ${green300};
    font-size: 0.95rem;
    font-weight: 400;
    padding: 6px 14px;
    margin-right: 8px;
    background: ${gray100};
    border: 0.5px solid ${green300};
    border-radius: 24px;

    &.btnViewMore {
      position: absolute;
      top: 140px;
      right: 100px;
      background: none;
      border: 0;
      padding: 0;
      margin: 0;
      font-weight: bold;
    }
  }
`;

const ViewMore = styled.div`
  position: relative;

  & button {
    position: absolute;
    top: 140px;
    right: 100px;
    background: none;
    border: 0;
    font-weight: bold;
  }
`;

function JourneyInfo({ journeyId, controlNickName, edit }) {
  const journeyInfo = useNewJourneyValue();
  const { initDatas } = useNewJourneyActions();
  const { getDatas, updateData, testSetData } = useJourneyDetailActions();
  const journey = useJourneyDetailValue();
  const [nickName, setNickName] = controlNickName;

  const [visible, setVisible] = useState(false);
  const [popOver, setPopOver] = useState(false);

  const handleOpenPopOver = () => {
    setPopOver(!popOver);
  };

  useEffect(() => {
    getDatas(journeyId);
  }, []);

  // 데이터 받아온 후 화면에 보여주기
  useEffect(() => {
    if (journey.journeyId) {
      if (journey.nickName !== nickName) {
        setNickName(journey.nickName);
      }
      setVisible(true);
    }
  }, [journey.journeyId]);

  return (
    <NewJourneyProvider>
      {visible && (
        <Container>
          <TitleArea />
          <ContentArea />
          {edit && (
            <div>
              <button
                type="button"
                className="btnViewMore"
                onClick={handleOpenPopOver}
              >
                더보기
              </button>
              {popOver && <ViewMorePopOver journeyId={journeyId} />}
            </div>
          )}
        </Container>
      )}
    </NewJourneyProvider>
  );
}

export default JourneyInfo;
