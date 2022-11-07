import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { authAPI } from '../lib/utills/http';

const AuthValueContext = createContext();
const AuthActionsContext = createContext();

const initialState = {
  id: '',
  email: '',
  name: '',
  nickName: '',
  profileImageUrl: '',
};

export default function AuthProvider({ children }) {
  const [userInfo, setUserInfo] = useState(initialState);

  const actions = useMemo(() => ({
    // 유저 인증
    async authenticateUser(accessToken) {
      try {
        const { id, email, name, nickName, profileImageUrl } = await authAPI(
          accessToken,
        );
        console.log(`id: ${id}`);
        setUserInfo((prev) => ({
          ...prev,
          id,
          email,
          name,
          nickName,
          profileImageUrl,
        }));
      } catch (err) {
        console.log(err);
      }
    },

    // 유저 정보 초기화
    initUserInfo() {
      setUserInfo(initialState);
    },
  }));

  return (
    <AuthActionsContext.Provider value={actions}>
      <AuthValueContext.Provider value={userInfo}>
        {children}
      </AuthValueContext.Provider>
    </AuthActionsContext.Provider>
  );
}

export const useAuthValue = () => {
  const value = useContext(AuthValueContext);
  if (value === undefined) {
    throw new Error('useAuthValue should be used within JourneysProvider');
  }
  return value;
};

export const useAuthActions = () => {
  const value = useContext(AuthActionsContext);
  if (value === undefined) {
    throw new Error('useAuthActions should be used within JourneysProvider');
  }
  return value;
};
