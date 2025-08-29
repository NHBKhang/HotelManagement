import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API, { endpoints } from "../../configs/API";

const SearchRooms = () => {
    const navigate = useNavigate();
    const [payload, setPayload] = useState({});
    const [types, setTypes] = useState([]);

    const updatePayload = (e) =>
        setPayload((p) => ({ ...p, [e.target.name]: e.target.value }));

    const onSearch = (e) => {
        e.preventDefault();
        const q = new URLSearchParams(payload).toString();
        navigate(`/search?${q}#results`);
    };

    const today = new Date().toISOString().split("T")[0];
    const minCheckout = payload.checkin || today;

    useEffect(() => {
        const loadTypes = async () => {
            try {
                const res = await API.get(endpoints["room-types"]);
                setTypes(res.data);
            } catch (error) {
                console.error(error);
            }
        }

        loadTypes();
    }, [])

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
                    name="checkIn"
                    onChange={updatePayload}
                    value={payload.checkIn}
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
                    name="checkOut"
                    onChange={updatePayload}
                    value={payload.checkOut}
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
                    name="roomTypeId"
                    onChange={updatePayload}
                    value={payload.roomTypeId}
                    className="mt-1 w-full rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 px-3 py-2 text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                >
                    <option value="">Tất cả</option>
                    {types.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
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
