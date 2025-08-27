import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import API, { endpoints } from "../configs/API";
import RoomCard from "../components/cards/RoomCard";
import SearchHero from "../components/ui/SearchHero";

const SearchPage = () => {
    const location = useLocation();
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const q = new URLSearchParams(location.search);
        const params = Object.fromEntries(q.entries());
        const loadRooms = async () => {
            setLoading(true);
            try {
                const res = await API.get(endpoints.rooms, { params });
                setRooms(res.data || []);
            } catch (err) {
                console.error(err);
                setRooms([]);
            } finally {
                setLoading(false);
            }
        };
        
        loadRooms();
    }, [location.search]);

    return (
        <div className="max-w-7xl mx-auto px-6 py-12">
            <SearchHero />
            {loading ? (
                <p className="text-center text-slate-500">Đang tải...</p>
            ) : rooms.length === 0 ? (
                <p className="text-center text-slate-500 mt-6">
                    Không tìm thấy phòng phù hợp với yêu cầu của bạn.
                </p>
            ) : (
                <div className="grid md:grid-cols-3 gap-6 my-5">
                    {rooms.map((r) => (
                        <RoomCard key={r.id} room={r} />
                    ))}
                </div>
            )}
        </div>
    );
};

export default SearchPage;
