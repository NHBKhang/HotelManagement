import React, { useState } from "react";
import InputField from "../components/ui/InputField";
import { useNavigate } from "react-router-dom";
import API, { endpoints } from "../configs/API";

const SignupPage = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        lastName: "",
        firstName: "",
        username: "",
        email: "",
        phone: "",
        password: "",
        confirmPassword: "",
    });

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const updateForm = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const validate = () => {
        let ok = true;
        const next = {};
        if (!form.lastName) {
            next.lastName = "Vui lòng nhập họ";
            ok = false;
        }
        if (!form.firstName) {
            next.firstName = "Vui lòng nhập tên";
            ok = false;
        }
        if (!form.username) {
            next.username = "Vui lòng nhập tên tài khoản";
            ok = false;
        }
        if (!form.email) {
            next.email = "Vui lòng nhập email";
            ok = false;
        }
        if (!form.phone) {
            next.phone = "Vui lòng nhập số điện thoại";
            ok = false;
        }
        if (!form.password) {
            next.password = "Vui lòng nhập mật khẩu";
            ok = false;
        }
        if (form.confirmPassword !== form.password) {
            next.confirmPassword = "Mật khẩu xác nhận không khớp";
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
            const formPayload = new FormData();
            for (const key in form) {
                let value = form[key]
                if (value && value.toString().trim() !== '')
                    formPayload.append(key, value);
            }

            let res = await API.post(endpoints.users, formPayload,
                {
                    headers: { "Content-Type": "mulitpart/form-data" }
                }
            );
            console.info(res);
            if (res.status === 201)
                navigate('/login');
            else
                setErrors({ submit: "Yêu cầu không hợp lệ!" })
        } catch (error) {
            if (error.response) {
                setErrors(prev => ({ ...prev, server: error.response.data?.message || "Đăng ký thất bại!" }));
            } else if (error.request) {
                setErrors(prev => ({ ...prev, server: "Không thể kết nối server, vui lòng thử lại!" }));
            } else {
                setErrors(prev => ({ ...prev, server: error.message }));
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen w-full px-5 bg-gradient-to-br from-indigo-50 via-white to-cyan-50 dark:from-slate-900 dark:via-slate-950 dark:to-slate-900 text-slate-900 dark:text-slate-100">
            <div className="relative mx-auto flex min-h-screen max-w-screen-xl items-center px-4">
                <div className="grid w-full grid-cols-1 gap-10 md:grid-cols-2">
                    <div className="flex flex-col justify-center">
                        <div className="inline-flex items-center gap-3 cursor-pointer justify-center md:justify-start mt-6 md:mt-0 mb-0 md:mb-6" 
                        onClick={() => navigate('/')}>
                            <div className="grid h-12 w-12 place-items-center rounded-2xl bg-slate-900 text-white shadow-lg dark:bg-white dark:text-slate-900">
                                <img src="./img/hotel-icon.png" className="p-1" alt="Logo" />
                            </div>
                            <div className="text-3xl font-extrabold tracking-tight md:text-4xl">
                                Smart Hotel
                            </div>
                        </div>
                        <p className="max-w-prose text-slate-600 dark:text-slate-300 hidden md:block">
                            Tạo tài khoản mới để bắt đầu hành trình cùng{" "}
                            <span className="font-semibold">Smart Hotel</span>.
                        </p>
                    </div>

                    <div className="flex items-center justify-center my-2">
                        <div className="w-full max-w-5xl my-5 rounded-2xl border border-slate-200 bg-white/80 p-8 shadow-xl backdrop-blur dark:border-slate-800 dark:bg-slate-900/80">
                            <h2 className="mb-2 text-center text-3xl font-bold">Đăng ký</h2>
                            <p className="mb-6 text-center text-sm text-slate-500 dark:text-slate-400">
                                Tạo tài khoản mới để trải nghiệm đầy đủ!
                            </p>

                            <form onSubmit={onSubmit} className="space-y-4">
                                <div className="grid grid-cols-2 gap-4">
                                    <InputField
                                        id="lastName"
                                        name="lastName"
                                        label="Họ"
                                        value={form.lastName}
                                        onChange={updateForm}
                                        placeholder="Nguyễn"
                                        icon="fa-user"
                                        error={errors.lastName}
                                    />
                                    <InputField
                                        id="firstName"
                                        name="firstName"
                                        label="Tên"
                                        value={form.firstName}
                                        onChange={updateForm}
                                        placeholder="An"
                                        icon="fa-user"
                                        error={errors.firstName}
                                    />
                                </div>

                                <InputField
                                    id="username"
                                    name="username"
                                    label="Tên tài khoản"
                                    value={form.username}
                                    onChange={updateForm}
                                    placeholder="username"
                                    icon="fa-user"
                                    error={errors.username}
                                />

                                <InputField
                                    id="email"
                                    name="email"
                                    type="email"
                                    label="Email"
                                    value={form.email}
                                    onChange={updateForm}
                                    placeholder="you@example.com"
                                    icon="fa-envelope"
                                    error={errors.email}
                                />

                                <InputField
                                    id="phone"
                                    name="phone"
                                    type="tel"
                                    label="Số điện thoại"
                                    value={form.phone}
                                    onChange={updateForm}
                                    placeholder="0123 456 789"
                                    icon="fa-phone"
                                    error={errors.phone}
                                />

                                <InputField
                                    id="password"
                                    name="password"
                                    type="password"
                                    label="Mật khẩu"
                                    value={form.password}
                                    onChange={updateForm}
                                    placeholder="******"
                                    icon="fa-lock"
                                    error={errors.password}
                                />

                                <InputField
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    type="password"
                                    label="Xác nhận mật khẩu"
                                    value={form.confirmPassword}
                                    onChange={updateForm}
                                    placeholder="******"
                                    icon="fa-check"
                                    error={errors.confirmPassword}
                                />

                                <div className="my-4 h-px bg-slate-200 dark:bg-slate-800" />

                                <div className=" text-center max-w-full">
                                    {errors.server && (
                                        <p className="mt-1 text-sm text-red-500">{errors.server}</p>
                                    )}
                                </div>

                                <button
                                    type="submit"
                                    className="w-full rounded-xl bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 active:scale-[.99] disabled:opacity-60"
                                    disabled={loading}
                                >
                                    {loading ? "Đang đăng ký..." : "Đăng ký"}
                                </button>

                                <p className="text-center text-sm text-slate-500 dark:text-slate-400">
                                    Đã có tài khoản?{" "}
                                    <a
                                        href="/login"
                                        className="font-medium text-indigo-600 hover:underline"
                                    >
                                        Đăng nhập
                                    </a>
                                </p>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SignupPage;
