import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import API, { endpoints, useAuthAPI } from "../configs/API";
import { useUserContext } from "../configs/UserContext";

const LoginPage = () => {
    const navigate = useNavigate();
    const { dispatch, saveToken } = useUserContext();
    const { get } = useAuthAPI();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({ username: "", password: "" });

    const validate = () => {
        let ok = true;
        const next = { username: "", password: "" };
        if (!username) {
            next.username = "Vui lòng nhập tên tài khoản";
            ok = false;
        }
        if (!password) {
            next.password = "Vui lòng nhập mật khẩu";
            ok = false;
        }
        setErrors(next);
        return ok;
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        if (!validate()) return;
        setLoading(true);
        try {
            let res = await API.post(endpoints.login, { username, password }, {
                headers: {
                    "Content-Type": "application/json"
                }
            });
            await saveToken(res.data);

            if (res.status === 200) {
                let res = await get(endpoints['current-user']);
                dispatch({ type: 'SET_USER', payload: res.data.user });

                navigate('/');
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400 || status === 401) {
                    setErrors(prev => ({ ...prev, server: data.message || "Tên đăng nhập hoặc mật khẩu không đúng!" }));
                } else {
                    setErrors(prev => ({ ...prev, server: data.message || "Lỗi máy chủ, vui lòng thử lại!" }));
                }
            } else if (error.request) {
                setErrors(prev => ({ ...prev, server: "Không thể kết nối đến server, vui lòng kiểm tra mạng!" }));
            } else {
                setErrors(prev => ({ ...prev, server: "Đã xảy ra lỗi không xác định!" }));
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen w-full px-5 bg-gradient-to-br from-slate-50 via-white to-slate-100 dark:from-slate-900 dark:via-slate-950 dark:to-slate-900 text-slate-900 dark:text-slate-100">
            <div className="pointer-events-none fixed inset-0 overflow-hidden">
                <div className="absolute -left-20 -top-20 h-72 w-72 rounded-full bg-indigo-200/40 blur-3xl dark:bg-indigo-500/20" />
                <div className="absolute -right-20 -bottom-20 h-72 w-72 rounded-full bg-cyan-200/40 blur-3xl dark:bg-cyan-500/20" />
            </div>

            <div className="relative mx-auto flex min-h-screen max-w-7xl items-center px-4">
                <div className="grid w-full grid-cols-1 gap-10 md:grid-cols-2">
                    <div className="flex flex-col justify-center">
                        <div className="inline-flex items-center gap-3 cursor-pointer md:text-center justify-center md:justify-start mt-6 md:mt-0 mb-0 md:mb-6"
                            onClick={() => navigate('/')}>
                            <div className="grid h-12 w-12 place-items-center rounded-2xl bg-slate-900 text-white shadow-lg dark:bg-white dark:text-slate-900">
                                <img src="./img/hotel-icon.png" className="p-1" alt="Logo" />
                            </div>
                            <div className="text-3xl font-extrabold tracking-tight md:text-4xl">
                                Smart Hotel
                            </div>
                        </div>
                        <p className="max-w-prose text-slate-600 dark:text-slate-300 hidden md:block">
                            Chào mừng trở lại 👋 Hãy đăng nhập để tiếp tục trải nghiệm cùng <span className="font-semibold">Smart Hotel</span>.
                        </p>
                    </div>

                    <div className="flex items-center justify-center">
                        <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white/80 p-6 shadow-xl backdrop-blur dark:border-slate-800 dark:bg-slate-900/80">
                            <h2 className="mb-1 text-center text-2xl font-bold">Đăng nhập</h2>
                            <p className="mb-6 text-center text-sm text-slate-500 dark:text-slate-400">
                                Sẵn sàng bắt đầu ngày mới nào!
                            </p>
                            <form onSubmit={onSubmit} className="space-y-4">
                                <div>
                                    <label className="mb-2 block text-sm font-medium" htmlFor="username">
                                        Tên tài khoản
                                    </label>
                                    <input
                                        id="name"
                                        type="text"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                        className={`w-full rounded-xl border bg-white px-4 py-3 outline-none transition focus:ring-4 dark:bg-slate-950 ${errors.username
                                            ? "border-red-400 focus:ring-red-100 dark:focus:ring-red-900"
                                            : "border-slate-200 focus:ring-indigo-100 dark:border-slate-800 dark:focus:ring-indigo-900"
                                            }`}
                                        autoComplete="username"
                                    />
                                    {errors.username && (
                                        <p className="mt-1 text-sm text-red-500">{errors.username}</p>
                                    )}
                                </div>

                                <div>
                                    <label className="mb-2 block text-sm font-medium" htmlFor="password">
                                        Mật khẩu
                                    </label>
                                    <div className="relative">
                                        <input
                                            id="password"
                                            type={showPassword ? "text" : "password"}
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            placeholder="******"
                                            className={`w-full rounded-xl border bg-white px-4 py-3 pr-12 outline-none transition focus:ring-4 dark:bg-slate-950 ${errors.password
                                                ? "border-red-400 focus:ring-red-100 dark:focus:ring-red-900"
                                                : "border-slate-200 focus:ring-indigo-100 dark:border-slate-800 dark:focus:ring-indigo-900"
                                                }`}
                                            autoComplete="current-password"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPassword((s) => !s)}
                                            className="absolute inset-y-0 right-2 grid w-10 place-items-center rounded-lg text-slate-500 hover:bg-slate-100 active:scale-95 dark:hover:bg-slate-800"
                                            aria-label={showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                                        >
                                            <i className={`fa ${showPassword ? "fa-eye-slash" : "fa-eye"}`} />
                                        </button>
                                    </div>
                                    {errors.password && (
                                        <p className="mt-1 text-sm text-red-500">{errors.password}</p>
                                    )}
                                </div>

                                <div className="flex items-center justify-between">
                                    <label className="inline-flex cursor-pointer items-center gap-2 text-sm">
                                        <input type="checkbox" className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500" />
                                        Ghi nhớ đăng nhập
                                    </label>
                                    <a href="/" className="text-sm font-medium text-indigo-600 hover:underline">Quên mật khẩu?</a>
                                </div>

                                <div className=" text-center max-w-full">
                                    {errors.server && (
                                        <p className="mt-1 text-sm text-red-500">{errors.server}</p>
                                    )}
                                </div>

                                <button
                                    type="submit"
                                    className="w-full rounded-xl bg-slate-900 px-4 py-3 font-semibold text-white transition hover:brightness-110 active:scale-[.99] disabled:opacity-60 dark:bg-white dark:text-slate-900"
                                    disabled={loading}
                                >
                                    {loading ? "Đang đăng nhập..." : "Đăng nhập"}
                                </button>

                                <div className="flex items-center gap-3">
                                    <div className="h-px flex-1 bg-slate-200 dark:bg-slate-800" />
                                    <span className="text-xs text-slate-500">hoặc</span>
                                    <div className="h-px flex-1 bg-slate-200 dark:bg-slate-800" />
                                </div>

                                <div className="grid grid-cols-2 gap-3">
                                    <button type="button" className="inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium transition hover:bg-slate-50 active:scale-95 dark:border-slate-800 dark:bg-slate-950">
                                        {/* Google icon */}
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" className="h-5 w-5"><path fill="#FFC107" d="M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8-6.627 0-12-5.373-12-12S17.373 12 24 12c3.059 0 5.84 1.154 7.961 3.039l5.657-5.657C34.534 6.053 29.534 4 24 4 12.955 4 4 12.955 4 24s8.955 20 20 20 20-8.955 20-20c0-1.341-.138-2.65-.389-3.917z" /><path fill="#FF3D00" d="M6.306 14.691l6.571 4.818C14.655 15.108 18.961 12 24 12c3.059 0 5.84 1.154 7.961 3.039l5.657-5.657C34.534 6.053 29.534 4 24 4 16.318 4 9.656 8.337 6.306 14.691z" /><path fill="#4CAF50" d="M24 44c5.166 0 9.86-1.977 13.409-5.197l-6.191-5.238C29.146 35.091 26.715 36 24 36c-5.202 0-9.618-3.317-11.283-7.957l-6.54 5.036C9.49 39.556 16.227 44 24 44z" /><path fill="#1976D2" d="M43.611 20.083H42V20H24v8h11.303c-.792 2.237-2.231 4.166-4.097 5.565.001-.001 6.196 5.24 6.196 5.24C39.141 36.817 44 31.333 44 24c0-1.341-.138-2.65-.389-3.917z" /></svg>
                                        Google
                                    </button>
                                    <button type="button" className="inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium transition hover:bg-slate-50 active:scale-95 dark:border-slate-800 dark:bg-slate-950">
                                        {/* GitHub icon */}
                                        <svg viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 fill-current"><path fillRule="evenodd" d="M8 0C3.58 0 0 3.58 0 8a8.01 8.01 0 0 0 5.47 7.59c.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.2 1.87.86 2.33.65.07-.52.28-.86.51-1.06-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.33-.27 2.01-.27.68 0 1.37.09 2.01.27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.19 0 .21.15.46.55.38A8.01 8.01 0 0 0 16 8c0-4.42-3.58-8-8-8Z" /></svg>
                                        GitHub
                                    </button>
                                </div>

                                <p className="text-center text-sm text-slate-500 dark:text-slate-400">
                                    Chưa có tài khoản?{' '}
                                    <a href="/signup" className="font-medium text-indigo-600 hover:underline">Đăng ký</a>
                                </p>
                            </form>
                        </div>
                    </div>
                </div>
            </div >
        </div >
    );
}

export default LoginPage;