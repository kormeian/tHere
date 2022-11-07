import axios from 'axios';
import React, {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { authAxios, baseAxios } from '../lib/utills/customAxios';

const JourneyListValueContext = createContext();
const JourneyListActionsContext = createContext();

const initialState = [];

function JourneyListProvider({ children }) {
  const [journeyList, setJourneyList] = useState(initialState);
  const [searchOptions, setSearchOptions] = useState({
    keyword: '',
    themes: '',
    regions: '',
  });

  const values = useMemo(
    () => [journeyList, searchOptions],
    [
      journeyList.length,
      searchOptions.keyword,
      searchOptions.regions,
      searchOptions.themes,
    ],
  );

  const actions = useMemo(() => ({
    async loadJourneyItems(options, page = 0) {
      const params = { ...options };
      Object.entries(params).forEach(([key, value]) => {
        if (!value.length) params[key] = '';
        else if (Array.isArray(value)) params[key] = params[key].join(',');
      });

      try {
        const { data } = await baseAxios.get(
          `/journey/filtered-list?page=${page}&size=6`,
          { params },
        );
        console.log(data);

        if (data) {
          setJourneyList((prev) => [...prev, ...data.content]);
        }

        return data.content.length < page;
      } catch (err) {
        console.log(err);
      }
    },

    // 페이징 기능 추가 필요
    loadMyJourneyItems(memberId, page) {
      authAxios
        .get(`/journey/my-list?memberId=${memberId}&size=15&page=${page}`)
        .then(({ data }) => {
          if (!data?.content?.length) return false;

          setJourneyList((prev) => [...prev, ...data.content]);
          return true;
        })
        .catch((err) => {
          const { errorCode, errorMessage } = err.response.data;
          console.log(errorCode);
          console.log(errorMessage);
        });
    },

    // 페이징 기능 추가 필요
    loadBookmarkedItems(memberId, page = 0) {
      authAxios
        .get(`/bookmark?memberId=${memberId}`)
        .then(({ data }) => {
          console.log(data);

          if (!data) return false;

          setJourneyList((prev) => [...prev, ...data]);
          return true;
        })
        .catch((err) => console.error(err));
    },

    // 여정 목록 초기화
    initDatas() {
      setJourneyList([]);
    },

    updateSearchOptions(name, value) {
      setSearchOptions((prev) => ({ ...prev, [name]: value }));
    },

    initSearchOptions() {
      setSearchOptions({ keyword: '', themes: '', regions: '' });
    },
  }));

  return (
    <JourneyListActionsContext.Provider value={actions}>
      <JourneyListValueContext.Provider value={values}>
        {children}
      </JourneyListValueContext.Provider>
    </JourneyListActionsContext.Provider>
  );
}

const useJourneyListValue = () => {
  const value = useContext(JourneyListValueContext);
  if (value === undefined) {
    throw new Error(
      'useJourneyListValue should be used within JourneysProvider',
    );
  }
  return value;
};

const useJourneyListActions = () => {
  const value = useContext(JourneyListActionsContext);
  if (value === undefined) {
    throw new Error(
      'useJourneyListActions should be used within JourneysProvider',
    );
  }
  return value;
};

export { useJourneyListValue, useJourneyListActions };
export default JourneyListProvider;
