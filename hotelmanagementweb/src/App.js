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
import { ProtectedRoute } from "./components/base/ProtectedRoute";
import PaymentPage from "./pages/PaymentPage";
import MyBookingPage from "./pages/MyBookingPage";
import { ToastContainer } from "react-toastify";
import BookingDetailPage from "./pages/BookingDetailPage";
import ProfilePage from "./pages/ProfilePage";

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
              <Route path="payment" element={
                <ProtectedRoute>
                  <PaymentPage />
                </ProtectedRoute>}
              />
              <Route path="my-bookings" element={
                <ProtectedRoute>
                  <MyBookingPage />
                </ProtectedRoute>}
              />
              <Route path="my-bookings/:id" element={
                <ProtectedRoute>
                  <BookingDetailPage />
                </ProtectedRoute>}
              />
              <Route path="profile" element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>}
              />
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
        <ToastContainer />
      </UserProvider>
    </CookiesProvider >
  );
}

export default App;
