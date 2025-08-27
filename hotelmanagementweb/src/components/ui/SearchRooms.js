import { useState } from "react";
import { useNavigate } from "react-router-dom";

const SearchRooms = () => {
    const navigate = useNavigate();
    const [payload, setPayload] = useState({
        checkin: "",
        checkout: "",
        type: "any",
        maxPrice: ""
    });

    const updatePayload = (e) =>
        setPayload((p) => ({ ...p, [e.target.name]: e.target.value }));

    const onSearch = (e) => {
        e.preventDefault();
        const q = new URLSearchParams(payload).toString();
        navigate(`/search?${q}`);
    };

    const today = new Date().toISOString().split("T")[0];
    const minCheckout = payload.checkin || today;

    return (
        <form
            onSubmit={onSearch}
            className="grid grid-cols-1 md:grid-cols-5 gap-4 items-end bg-white dark:bg-slate-900 p-6 rounded-2xl shadow-lg"
        >
            <div>
                <label className="text-sm font-medium text-slate-600 dark:text-slate-300">
                    Nhận phòng
                </label>
                <input
                    name="checkin"
                    onChange={updatePayload}
                    value={payload.checkin}
                    type="date"
                    min={today}
                    className="mt-1 w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 px-3 py-2 text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                />
            </div>

            <div>
                <label className="text-sm font-medium text-slate-600 dark:text-slate-300">
                    Trả phòng
                </label>
                <input
                    name="checkout"
                    onChange={updatePayload}
                    value={payload.checkout}
                    type="date"
                    min={minCheckout}
                    className="mt-1 w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 px-3 py-2 text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                />
            </div>

            <div>
                <label className="text-sm font-medium text-slate-600 dark:text-slate-300">
                    Loại phòng
                </label>
                <select
                    name="type"
                    onChange={updatePayload}
                    value={payload.type}
                    className="mt-1 w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 px-3 py-2 text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                >
                    <option value="any">Tất cả</option>
                    <option value="single">Phòng đơn</option>
                    <option value="double">Phòng đôi</option>
                    <option value="suite">Suite</option>
                </select>
            </div>

            <div>
                <label className="text-sm font-medium text-slate-600 dark:text-slate-300">
                    Giá tối đa
                </label>
                <input
                    name="maxPrice"
                    onChange={updatePayload}
                    value={payload.maxPrice}
                    type="number"
                    placeholder="VNĐ"
                    min={1000}
                    max={10000000}
                    className="mt-1 w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 px-3 py-2 text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                />
            </div>

            <div>
                <button
                    type="submit"
                    className="w-full rounded-lg bg-indigo-600 px-4 py-2 font-semibold text-white hover:bg-indigo-700 active:scale-[.98] transition"
                >
                    Tìm phòng
                </button>
            </div>
        </form>
    );
};

export default SearchRooms;
