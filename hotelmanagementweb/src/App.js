import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import { CookiesProvider } from "react-cookie";
import { UserProvider } from "./configs/UserContext";
import HomePage from "./pages/HomePage";
import { GuestRoute } from "./components/base/GuestRoute";
import Layout from "./components/base/Layout";
import SearchPage from "./pages/SearchPage";
import RoomPage from "./pages/RoomPage";


function App() {
  return (
    <CookiesProvider defaultSetOptions={{ path: '/' }}>
      <UserProvider>
        <Router>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<HomePage />} />
              <Route path="search" element={<SearchPage />} />
              <Route path="rooms/:id" element={<RoomPage />} />
            </Route>
            <Route path='login' element={
              <GuestRoute>
                <LoginPage />
              </GuestRoute>
            } />
            <Route path='signup' element={
              <GuestRoute>
                <SignupPage />
              </GuestRoute>
            } />
          </Routes>
        </Router>
      </UserProvider>
    </CookiesProvider >
  );
}

export default App;
