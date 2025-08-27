import React from "react";
import { useNavigate } from "react-router-dom";
import { useUserContext } from "../configs/UserContext";

const HomePage = () => {
    const navigate = useNavigate();
    const { state } = useUserContext();

    const user = state?.currentUser;

    return (
        <>
            <section className="relative flex flex-col items-center justify-center text-center px-6 py-20 bg-gradient-to-r from-indigo-100 via-white to-cyan-100 dark:from-slate-900 dark:via-slate-950 dark:to-slate-900">
                {user ? (
                    <>
                        <h1 className="text-4xl md:text-5xl font-bold mb-4">
                            Xin chào <span className="text-indigo-600">{user.firstName || user.username}</span> 👋
                        </h1>
                        <p className="text-slate-600 dark:text-slate-300 max-w-2xl mb-8">
                            Chúc bạn một ngày tốt lành! Truy cập nhanh các chức năng quản lý khách sạn của bạn.
                        </p>
                        <div className="flex gap-4">
                            <button
                                onClick={() => navigate("/dashboard")}
                                className="rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white hover:bg-indigo-700"
                            >
                                Tới Dashboard
                            </button>
                            <button
                                onClick={() => navigate("/profile")}
                                className="rounded-xl border border-slate-300 px-6 py-3 font-semibold text-slate-900 hover:bg-slate-100 dark:border-slate-700 dark:text-slate-100 dark:hover:bg-slate-800"
                            >
                                Hồ sơ cá nhân
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <h1 className="text-4xl md:text-5xl font-bold mb-4">
                            Trải nghiệm <span className="text-indigo-600">khách sạn thông minh</span>
                        </h1>
                        <p className="text-slate-600 dark:text-slate-300 max-w-2xl mb-8">
                            Đặt phòng nhanh chóng, tiện lợi, quản lý dễ dàng chỉ với vài thao tác.
                        </p>
                        <div className="flex gap-4">
                            <button
                                onClick={() => navigate("/signup")}
                                className="rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white hover:bg-indigo-700"
                            >
                                Bắt đầu ngay
                            </button>
                            <button
                                onClick={() => navigate("/login")}
                                className="rounded-xl border border-slate-300 px-6 py-3 font-semibold text-slate-900 hover:bg-slate-100 dark:border-slate-700 dark:text-slate-100 dark:hover:bg-slate-800"
                            >
                                Đăng nhập
                            </button>
                        </div>
                    </>
                )}
            </section>

            <section
                id="services"
                className="max-w-6xl mx-auto px-6 py-16 grid md:grid-cols-3 gap-10"
            >
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="font-semibold text-lg mb-2">Đặt phòng nhanh</h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Tìm và đặt phòng chỉ trong vài giây với giao diện trực quan.
                    </p>
                </div>
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="font-semibold text-lg mb-2">Thanh toán tiện lợi</h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Hỗ trợ nhiều phương thức thanh toán an toàn và nhanh chóng.
                    </p>
                </div>
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="font-semibold text-lg mb-2">Quản lý thông minh</h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Theo dõi lịch đặt, quản lý thông tin cá nhân dễ dàng.
                    </p>
                </div>
            </section>
        </>
    );
};

export default HomePage;
