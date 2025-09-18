import { useState } from "react";
import { Edit, LogOut, Mail, MapPin, Phone, User, X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useUserContext } from "../configs/UserContext";
import { endpoints, useAuthAPI } from "../configs/API";
import { useNotification } from "../utils/toast";

const ProfilePage = () => {
    const authAPI = useAuthAPI();
    const sendNotification = useNotification();
    const { state, logout, dispatch } = useUserContext();
    const [user, setUser] = useState(state?.currentUser);

    const [isEditing, setIsEditing] = useState(false);
    const [data, setData] = useState({
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || "",
        phone: user.phone || "",
        address: user.address || ""
    });

    const updateData = (field, value) => {
        setData(prev => ({ ...prev, [field]: value }));
    }

    const saveUser = async () => {
        try {
            let res = await authAPI.patch(endpoints["current-user"], data);

            dispatch({ type: 'SET_USER', payload: res.data.user });
            setUser(res.data.user);
            setIsEditing(false);
        } catch (error) {
            console.error(error);
            sendNotification({ message: "Lỗi xảy ra khi cập nhật thông tin!" }, 'error')
        }
    };

    return (
        <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-cyan-50 dark:from-gray-900 dark:via-gray-950 dark:to-gray-900 p-6 transition-colors duration-300">
            <div className="max-w-lg w-full shadow-xl rounded-2xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
                <div className="p-6">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="flex flex-col items-center text-center"
                    >
                        <img
                            src={user.avatar ?? 'https://icons.iconarchive.com/icons/papirus-team/papirus-status/512/avatar-default-icon.png'}
                            alt="avatar"
                            className="w-28 h-28 rounded-full shadow-md object-cover border-4 border-white dark:border-gray-800 -mt-16 mb-4"
                        />
                        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                            {user.fullName}
                        </h1>
                        <p className="text-gray-500 dark:text-gray-400"></p>
                    </motion.div>

                    <div className="mt-6 space-y-3 text-sm">
                        <div className="flex items-center gap-2">
                            <User className="w-4 h-4 text-gray-400 dark:text-gray-500" />
                            <span className="text-gray-700 dark:text-gray-300">
                                {user.username}
                            </span>
                        </div>
                        <div className="flex items-center gap-2">
                            <Mail className="w-4 h-4 text-gray-400 dark:text-gray-500" />
                            <span className="text-gray-700 dark:text-gray-300">
                                {user.email || "Chưa có email"}
                            </span>
                        </div>
                        <div className="flex items-center gap-2">
                            <Phone className="w-4 h-4 text-gray-400 dark:text-gray-500" />
                            <span className="text-gray-700 dark:text-gray-300">
                                {user.phone || "Chưa có số điện thoại"}
                            </span>
                        </div>
                        {user.address && (
                            <div className="flex items-center gap-2">
                                <MapPin className="w-4 h-4 text-gray-400 dark:text-gray-500" />
                                <span className="text-gray-700 dark:text-gray-300">
                                    {user.address}
                                </span>
                            </div>
                        )}
                    </div>

                    <div className="mt-6 flex gap-3">
                        <button
                            onClick={() => setIsEditing(true)}
                            className="flex-1 flex items-center justify-center gap-2 bg-indigo-500 hover:bg-indigo-600 text-white py-2 rounded-xl shadow-md transition-colors"
                        >
                            <Edit className="w-4 h-4" />
                            Chỉnh sửa
                        </button>
                        <button
                            onClick={logout}
                            className="flex-1 flex items-center justify-center gap-2 border border-red-300 dark:border-red-400 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 py-2 rounded-xl shadow-sm transition-colors"
                        >
                            <LogOut className="w-4 h-4" />
                            Đăng xuất
                        </button>
                    </div>
                </div>
            </div>

            <AnimatePresence>
                {isEditing && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 flex items-center justify-center bg-black/50 z-50"
                    >
                        <motion.div
                            initial={{ scale: 0.8, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.8, opacity: 0 }}
                            className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-xl w-full max-w-md"
                        >
                            <div className="flex justify-between items-center mb-4">
                                <h2 className="text-xl font-bold text-gray-800 dark:text-gray-100">
                                    Chỉnh sửa thông tin
                                </h2>
                                <button onClick={() => setIsEditing(false)}>
                                    <X className="w-5 h-5 text-gray-500 hover:text-gray-700 dark:hover:text-gray-300" />
                                </button>
                            </div>

                            <div className="space-y-3">
                                <div className="grid grid-cols-2 gap-3">
                                    <input
                                        type="text"
                                        value={data.firstName}
                                        onChange={(e) => updateData('firstName', e.target.value)}
                                        placeholder="Họ"
                                        className="px-3 py-2 border rounded-lg w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                    />
                                    <input
                                        type="text"
                                        value={data.lastName}
                                        onChange={(e) => updateData('lastName', e.target.value)}
                                        placeholder="Tên"
                                        className="px-3 py-2 border rounded-lg w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                    />
                                </div>
                                <input
                                    type="email"
                                    value={data.email}
                                    onChange={(e) => updateData('email', e.target.value)}
                                    placeholder="Email"
                                    className="px-3 py-2 border rounded-lg w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                />
                                <input
                                    type="text"
                                    value={data.phone}
                                    onChange={(e) => updateData('phone', e.target.value)}
                                    placeholder="Số điện thoại"
                                    className="px-3 py-2 border rounded-lg w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                />
                                <input
                                    type="text"
                                    value={data.address}
                                    onChange={(e) => updateData('address', e.target.value)}
                                    placeholder="Địa chỉ"
                                    className="px-3 py-2 border rounded-lg w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                />
                            </div>

                            <div className="flex justify-end gap-3 mt-5">
                                <button
                                    onClick={() => setIsEditing(false)}
                                    className="px-4 py-2 rounded-lg border dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                                >
                                    Hủy
                                </button>
                                <button
                                    onClick={saveUser}
                                    className="px-4 py-2 rounded-lg bg-indigo-500 hover:bg-indigo-600 text-white shadow"
                                >
                                    Lưu
                                </button>
                            </div>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default ProfilePage;
