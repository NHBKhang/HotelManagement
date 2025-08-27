import { Navigate } from "react-router-dom";
import { useUserContext } from "../../configs/UserContext";

export const GuestRoute = ({ children }) => {
    const { state } = useUserContext();
    return state?.currentUser ? <Navigate to="/" replace /> : children;
};
