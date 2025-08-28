import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import API, { endpoints } from "../configs/API";
import RoomCard from "../components/cards/RoomCard";
import SearchHero from "../components/ui/SearchHero";
import Pagination from "../components/ui/Pagination";

const SearchPage = () => {
    const location = useLocation();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [pageSize] = useState(6);

    useEffect(() => {
        const q = new URLSearchParams(location.search);
        const params = Object.fromEntries(q.entries());

        const loadRooms = async () => {
            setLoading(true);
            try {
                const res = await API.get(endpoints.rooms, {
                    params: { ...params, page, pageSize },
                });
                setData(res.data || null);
            } catch (err) {
                console.error(err);
                setData(null);
            } finally {
                setLoading(false);
            }
        };

        loadRooms();
    }, [location.search, page, pageSize]);

    return (
        <div className="max-w-7xl mx-auto px-6 py-12">
            <SearchHero />
            {loading ? (
                <p className="text-center text-slate-500">Đang tải...</p>
            ) : !data || data.results.length === 0 ? (
                <p className="text-center text-slate-500 mt-6">
                    Không tìm thấy phòng phù hợp với yêu cầu của bạn.
                </p>
            ) : (
                <>
                    <div className="grid md:grid-cols-3 gap-6 mb-5 mt-7">
                        {data.results.map((r) => (
                            <RoomCard key={r.id} room={r} />
                        ))}
                    </div>

                    <Pagination
                        current={data.current}
                        total={data.total}
                        onPageChange={setPage}
                        totalElements={data.totalElements}
                    />

                    <p className="text-center text-sm text-slate-500 mt-3">
                        Hiển thị {data.results.length} / {data.totalElements} phòng
                        (mỗi trang {data.size} phòng)
                    </p>
                </>
            )}
        </div>
    );
};

export default SearchPage;
