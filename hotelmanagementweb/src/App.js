import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import { CookiesProvider } from "react-cookie";
import { UserProvider } from "./configs/UserContext";
import HomePage from "./pages/HomePage";


function App() {
  return (
    <CookiesProvider defaultSetOptions={{ path: '/' }}>
      <UserProvider>
        <Router>
          <Routes>
            <Route path="/" element={<HomePage />}>
            </Route>
            <Route path='login' element={<LoginPage />} />
            <Route path='signup' element={<SignupPage />} />
          </Routes>
        </Router>
      </UserProvider>
    </CookiesProvider>
  );
}

export default App;
