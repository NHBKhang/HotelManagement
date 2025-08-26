import React, { useState } from "react";

const InputField = ({
    id,
    name,
    type = "text",
    label,
    value,
    onChange,
    placeholder,
    icon,
    error,
}) => {
    const [showPassword, setShowPassword] = useState(false);

    const inputType =
        type === "password" ? (showPassword ? "text" : "password") : type;

    return (
        <div>
            {label && (
                <label className="mb-2 block text-sm font-medium" htmlFor={id}>
                    {label}
                </label>
            )}
            <div className="relative">
                <input
                    id={id}
                    name={name}
                    type={inputType}
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                    className={`w-full rounded-xl border px-4 py-3 pl-10 pr-10 outline-none transition text-black
            ${error
                            ? "border-red-400 focus:ring-red-100"
                            : "border-slate-200 focus:ring-indigo-100"
                        }`}
                />
                {icon && (
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
                        <i className={`fa ${icon}`} />
                    </span>
                )}
                {type === "password" && (
                    <button
                        type="button"
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400"
                        onClick={() => setShowPassword((prev) => !prev)}
                    >
                        <i className={`fa ${showPassword ? "fa-eye-slash" : "fa-eye"}`} />
                    </button>
                )}
            </div>
            {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
        </div>
    );
};

export default InputField;
