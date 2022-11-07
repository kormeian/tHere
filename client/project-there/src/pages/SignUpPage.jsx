import React from 'react';
import { useLocation } from 'react-router-dom';
import SignUp from '../components/signUp/SignUp';

function SignUpPage() {
  const { state } = useLocation();

  return <SignUp info={state || ''} />;
}

export default SignUpPage;
