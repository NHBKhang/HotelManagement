import { Navigate } from "react-router-dom";
import { useUserContext } from "../../configs/UserContext";

export const ProtectedRoute = ({ children }) => {
    const { state } = useUserContext();
    return state?.currentUser ? children : <Navigate to="/login" replace />;
};
