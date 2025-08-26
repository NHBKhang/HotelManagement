import React from "react";
import { useNavigate } from "react-router-dom";

const HomePage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-slate-50 via-white to-slate-100 dark:from-slate-900 dark:via-slate-950 dark:to-slate-900 text-slate-900 dark:text-slate-100">
            <div className="pointer-events-none fixed inset-0 overflow-hidden">
                <div className="absolute -left-20 -top-20 h-72 w-72 rounded-full bg-indigo-200/40 blur-3xl dark:bg-indigo-500/20" />
                <div className="absolute -right-20 -bottom-20 h-72 w-72 rounded-full bg-cyan-200/40 blur-3xl dark:bg-cyan-500/20" />
            </div>

            <div className="relative flex flex-col items-center justify-center min-h-screen px-4">
                <div
                    className="mb-6 inline-flex items-center gap-3 cursor-pointer"
                    onClick={() => navigate("/")}
                >
                    <div className="grid h-12 w-12 place-items-center rounded-2xl bg-slate-900 text-white shadow-lg dark:bg-white dark:text-slate-900">
                        <img src="./img/hotel-icon.png" className="p-1" alt="Logo" />
                    </div>
                    <div className="text-3xl font-extrabold tracking-tight md:text-4xl">
                        Smart Hotel
                    </div>
                </div>

                <h1 className="text-4xl md:text-5xl font-bold text-center mb-4">
                    Chào mừng đến với <span className="text-indigo-600">Smart Hotel</span>
                </h1>
                <p className="text-slate-600 dark:text-slate-300 text-center max-w-2xl mb-8">
                    Trải nghiệm hệ thống quản lý khách sạn thông minh, hiện đại và tiện
                    lợi. Đăng nhập để bắt đầu hành trình của bạn!
                </p>

                <div className="flex gap-4">
                    <button
                        onClick={() => navigate("/login")}
                        className="rounded-xl bg-slate-900 px-6 py-3 font-semibold text-white transition hover:brightness-110 active:scale-[.98] dark:bg-white dark:text-slate-900"
                    >
                        Đăng nhập
                    </button>
                    <button
                        onClick={() => navigate("/signup")}
                        className="rounded-xl border border-slate-300 px-6 py-3 font-semibold text-slate-900 transition hover:bg-slate-100 active:scale-[.98] dark:border-slate-700 dark:text-slate-100 dark:hover:bg-slate-800"
                    >
                        Đăng ký
                    </button>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
