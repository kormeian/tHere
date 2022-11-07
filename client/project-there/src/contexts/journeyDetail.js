import axios from 'axios';
import React, {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { baseAxios } from '../lib/utills/customAxios';

const JourneyDetailValueContext = createContext();
const JourneyDetailActionsContext = createContext();

const initialState = {
  journeyId: 0,
  memberId: '1',
  title: '',
  startDate: '',
  endDate: '',
  numberOfPeople: 0,
  disclosure: '',
  journeyThumbnailUrl: '',
  introductionText: '',
  journeyThemes: [],
  region: '',
};

function JourneyDetailProvider({ children }) {
  const [journey, setJourney] = useState(initialState);
  const actions = useMemo(() => ({
    getDatas(jounreyId) {
      if (!jounreyId) {
        throw new Error('journeyId does not exist');
      }

      baseAxios
        .get(`/journey/detail?journeyId=${jounreyId}`)
        .then(({ data }) => {
          setJourney({ ...data });
        })
        .catch((err) => console.error(err));
    },

    updateData(name, value) {
      setJourney((prev) => ({ ...prev, [name]: value }));
    },
  }));

  return (
    <JourneyDetailActionsContext.Provider value={actions}>
      <JourneyDetailValueContext.Provider value={journey}>
        {children}
      </JourneyDetailValueContext.Provider>
    </JourneyDetailActionsContext.Provider>
  );
}

const useJourneyDetailValue = () => {
  const value = useContext(JourneyDetailValueContext);
  if (value === undefined) {
    throw new Error(
      'useJourneyDetailValue should be used within JourneysProvider',
    );
  }
  return value;
};

const useJourneyDetailActions = () => {
  const value = useContext(JourneyDetailActionsContext);
  if (value === undefined) {
    throw new Error(
      'useJourneyDetailActions should be used within JourneysProvider',
    );
  }
  return value;
};

export { useJourneyDetailValue, useJourneyDetailActions };
export default JourneyDetailProvider;
