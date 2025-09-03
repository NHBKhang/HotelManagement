import { useState } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import { useUserContext } from "../../configs/UserContext";

const Layout = () => {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();
    const { state, logout } = useUserContext();

    const user = state?.currentUser;

    const onLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="min-h-screen flex flex-col bg-white dark:bg-slate-950 text-slate-900 dark:text-slate-100">
            <header className="w-full border-b bg-white/80 backdrop-blur dark:bg-slate-900/80 sticky top-0 z-50">
                <div className="w-full px-6 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate("/")}>
                        <img src="/img/hotel-icon.png" alt="logo" className="h-10 w-10 p-1 rounded-lg bg-white" />
                        <span className="text-xl font-bold">Smart Hotel</span>
                    </div>

                    <nav className="hidden md:flex gap-6">
                        <a href="#about" className="hover:text-indigo-600">Giới thiệu</a>
                        <Link to="/search" className="hover:text-indigo-600">Tìm phòng</Link>
                        <Link to="/services" className="hover:text-indigo-600">Dịch vụ</Link>
                        <a href="#contact" className="hover:text-indigo-600">Liên hệ</a>
                    </nav>

                    <div className="flex items-center gap-3">
                        {user ? (
                            <>
                                <div className="relative">
                                    <button
                                        onClick={() => setOpen(!open)}
                                        className="flex items-center gap-2 p-1 rounded-md hover:bg-slate-100 dark:hover:bg-gray-700"
                                    >
                                        <img
                                            src={user.avatar || "https://icons.iconarchive.com/icons/papirus-team/papirus-status/512/avatar-default-icon.png"}
                                            alt="avatar"
                                            className="w-8 h-8 rounded-full object-cover border"
                                        />
                                        <span className="hidden md:inline font-medium text-md">
                                            {user.firstName || user.username}
                                        </span>
                                    </button>

                                    {open && (
                                        <div className="absolute right-0 mt-2 w-40 bg-white dark:bg-gray-800 rounded-lg shadow-lg border dark:border-gray-700">
                                            <Link
                                                to="/my-bookings"
                                                className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-700"
                                                onClick={() => setOpen(false)}
                                            >
                                                Đơn của tôi
                                            </Link>
                                            <button
                                                onClick={() => {
                                                    onLogout();
                                                    setOpen(false);
                                                }}
                                                className="w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                                            >
                                                Đăng xuất
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </>
                        ) : (
                            <>
                                <Link to="/login" className="px-3 py-2 rounded-md border">Đăng nhập</Link>
                                <Link to="/signup" className="px-4 py-2 rounded-md bg-indigo-600 text-white">Đăng ký</Link>
                            </>
                        )}
                    </div>
                </div>
            </header>

            <main className="flex-1">
                <Outlet />
            </main>

            <footer className="border-t border-slate-200 bg-white/80 dark:bg-slate-900/80 dark:border-slate-800">
                <div className="w-full px-6 py-6 flex flex-col md:flex-row items-center justify-between gap-4 text-sm text-slate-500 dark:text-slate-400">
                    <span>© {new Date().getFullYear()} Smart Hotel. All rights reserved.</span>
                    <div className="flex gap-4">
                        <a href="/privacy" className="hover:underline">
                            Chính sách
                        </a>
                        <a href="/terms" className="hover:underline">
                            Điều khoản
                        </a>
                        <a href="/contact" className="hover:underline">
                            Liên hệ
                        </a>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Layout;
