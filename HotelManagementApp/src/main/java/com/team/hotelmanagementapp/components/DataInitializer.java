package com.team.hotelmanagementapp.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.pojo.Invoice;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.repositories.FeedbackRepository;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import com.team.hotelmanagementapp.repositories.ServiceRepository;
import com.team.hotelmanagementapp.services.UserService;

import jakarta.annotation.PostConstruct;

@Component
@DependsOn("userServiceImpl")
public class DataInitializer {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @PostConstruct
    public void init() {
        // Create users
        User admin = null, customer = null;
        if (userService.getByUsername("admin") == null) {
            admin = new User("admin", "admin", "admin@gmail.com", User.Role.ADMIN, "Nguyễn", "Admin", "0123456789");
            customer = new User("customer", "customer", "customer@gmail.com", User.Role.CUSTOMER, "Trần Thị", "Na", "0967456615");

            userService.createOrUpdate(admin);
            userService.createOrUpdate(customer);
        } else {
            admin = userService.getByUsername("admin");
            customer = userService.getByUsername("customer");
        }

        // Create room types
        if (roomTypeRepository.findAll().isEmpty()) {
            RoomType standard = roomTypeRepository.createOrUpdate(new RoomType(null, "Tiêu chuẩn", "Phòng thoải mái với các tiện nghi cơ bản", 500000.0, 2));
            RoomType deluxe = roomTypeRepository.createOrUpdate(new RoomType(null, "Sang trọng", "Phòng rộng rãi với tiện nghi cao cấp", 800000.0, 3));
            RoomType suite = roomTypeRepository.createOrUpdate(new RoomType(null, "Suite", "Phòng suite sang trọng có phòng khách riêng biệt", 1200000.0, 4));

            // Create rooms
            for (int i = 1; i <= 10; i++) {
                RoomType type = i <= 6 ? standard : (i <= 8 ? deluxe : suite);
                roomRepository.createOrUpdate(new Room(null, "1" + String.format("%02d", i), type, Room.Status.AVAILABLE));
            }

            // Create sample bookings
            Room room1 = roomRepository.findAll().get(0);
            Room room2 = roomRepository.findAll().get(1);

            bookingRepository.createOrUpdate(new Booking(null, customer, room1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), Booking.Status.CONFIRMED, "Early check-in requested", 2));
            bookingRepository.createOrUpdate(new Booking(null, customer, room2, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), Booking.Status.PENDING, null, 3));
        }

        // Create hotel services
        if (serviceRepository.find(null).isEmpty()) {
            serviceRepository.createOrUpdate(new Service(null, "Bữa sáng", "Bữa sáng", 100000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Bữa trưa", "Bữa trưa buffet", 150000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Bữa tối", "Bữa tối buffet", 200000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Spa thư giãn", "Phiên spa thư giãn 1 giờ", 500000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Phục vụ phòng", "Phục vụ đồ uống và ăn nhẹ tại phòng", 250000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Giặt ủi", "Giặt ủi 1 bộ trang phục", 80000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Gym & Fitness", "Vé tập gym 1 ngày", 120000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Internet Wifi", "Internet tốc độ cao 1 ngày", 50000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Chỗ đậu xe", "Đậu xe an toàn 1 ngày", 30000.0, true));
            serviceRepository.createOrUpdate(new Service(null, "Dịch vụ đưa đón", "Đưa đón sân bay bằng xe riêng", 350000.0, true));
        }

        // Create feedback data
        if (feedbackRepository.find(null).isEmpty()) {
            // Get all bookings for feedback
            List<Booking> bookings = bookingRepository.findAll();

            if (!bookings.isEmpty()) {
                // Create feedback for completed bookings (simulate past guests)
                String[] positiveComments = {
                    "Phòng rất sạch sẽ, nhân viên hữu ích. Tôi sẽ quay trở lại!",
                    "Vị trí trung tâm, tiện đi lại. Dịch vụ xuất sắc!",
                    "Bữa sáng ngon, nội thất đẹp. Giá cả hợp lý.",
                    "Nhân viên thân thiện, phòng yên tĩnh. Ngủ rất ngon!",
                    "Tất cả đều tốt, đặc biệt là dịch vụ spa và phòng sạch sẽ.",
                    "Kinh nghiệm tuyệt vời! Bên khách sạn chuyên nghiệp.",
                    "Phòng ấm cúng, giường êm ái. Nhân viên rất cậu nào!",
                    "Dịch vụ tuyệt vời, tôi đã giới thiệu bạn bè đến đây.",
                    "Từ lúc nhận phòng đến trả phòng đều mượt mà. Tin cậy!",
                    "Bữa ăn ngon, hồ bơi đẹp. Kỳ nghỉ đáng nhớ!"
                };

                String[] serviceComments = {
                    "Wifi hơi chậm nhưng tổng thể tốt",
                    "Bữa sáng khá đa dạng, nhân viên tại nhà hàng thân thiện",
                    "Phòng ốc rất sạch, cách âm tốt, giường ngủ êm",
                    "Vị trí thuận tiện, gần sân bay và khu thương mại",
                    "Nhân viên lễ tân rất thân thiện và hữu ích",
                    "Cách bàn check in, check out rất nhanh chóng",
                    "Phảm chất dịch vụ chuyên nghiệp và chu đáo",
                    "Bữa tối phong phú, đầu bếp nấu ăn rất ngon",
                    "Hồ bơi sạch sẽ, nước đầy đủ khi tôi đến bơi không"
                };

                String[] constructiveComments = {
                    "Wifi tốc độ hơi chậm, mong được cải thiện",
                    "Xếp hạng sao thực sự thích hợp, đáng để ở lại",
                    "Giá cả khá mềm, sẽ đến ở lại khách sạn vào dịp sau",
                    "Nhân viên tin cậy nhất, tác lớn rất chuyên nghiệp",
                    "Chất lượng dịch vụ vượt trội, tôi hài lòng với sự đón tiếp",
                    "Đặt phòng dễ dàng, thủ tục xong xuôi rút gọn",
                    "Dịch vụ phòng khá được, ưu đãi giá rất hấp dẫn",
                    "Nhà hàng ngon miệng, mặt bằng sạch sẽ lịch sự"
                };

                // Create feedback for first booking (completed)
                if (bookings.size() >= 2) {
                    Booking completedBooking = bookings.get(1);
                    completedBooking.setStatus(Booking.Status.CHECKED_OUT); // Simulate completion
                    Booking booking = bookingRepository.createOrUpdate(completedBooking);

                    // Add multiple feedback for completed booking
                    feedbackRepository.createOrUpdate(new Feedback(null, booking, booking.getUser(),
                            4.5, positiveComments[0], LocalDateTime.now().minusDays(30)));

                    feedbackRepository.createOrUpdate(new Feedback(null, booking, customer,
                            4.0, positiveComments[1], LocalDateTime.now().minusDays(25)));
                }

                // Create standalone completed booking with feedback
                Room sampleRoom = roomRepository.findAll().get(0);
                Booking pastBooking = new Booking(null, customer, sampleRoom,
                        LocalDate.now().minusDays(10), LocalDate.now().minusDays(8),
                        Booking.Status.CHECKED_OUT, "Đặt phòng qua ứng dụng", 2);
                pastBooking = bookingRepository.createOrUpdate(pastBooking);

                // Add diverse feedback ratings
                feedbackRepository.createOrUpdate(new Feedback(null, pastBooking, customer,
                        5.0, positiveComments[2], LocalDateTime.now().minusDays(8)));

                feedbackRepository.createOrUpdate(new Feedback(null, pastBooking, customer,
                        3.5, serviceComments[0], LocalDateTime.now().minusDays(6)));

                feedbackRepository.createOrUpdate(new Feedback(null, pastBooking, customer,
                        4.5, positiveComments[3], LocalDateTime.now().minusDays(4)));

                // Create second past booking with feedback
                Room anotherRoom = roomRepository.findAll().get(1);
                Booking secondBooking = new Booking(null, customer, anotherRoom,
                        LocalDate.now().minusDays(20), LocalDate.now().minusDays(18),
                        Booking.Status.CHECKED_OUT, null, 2);
                secondBooking = bookingRepository.createOrUpdate(secondBooking);

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer,
                        4.0, serviceComments[1], LocalDateTime.now().minusDays(16)));

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer,
                        3.0, constructiveComments[0], LocalDateTime.now().minusDays(15)));

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer,
                        4.5, positiveComments[4], LocalDateTime.now().minusDays(14)));

                // Create third booking with mixed reviews
                Room thirdRoom = roomRepository.findAll().get(2);
                Booking thirdBooking = new Booking(null, customer, thirdRoom,
                        LocalDate.now().minusDays(35), LocalDate.now().minusDays(32),
                        Booking.Status.CHECKED_OUT, "Yêu cầu thêm ga gối", 4);
                thirdBooking = bookingRepository.createOrUpdate(thirdBooking);

                feedbackRepository.createOrUpdate(new Feedback(null, thirdBooking, customer,
                        4.5, positiveComments[5], LocalDateTime.now().minusDays(32)));

                feedbackRepository.createOrUpdate(new Feedback(null, thirdBooking, customer,
                        2.5, constructiveComments[1], LocalDateTime.now().minusDays(30)));

                feedbackRepository.createOrUpdate(new Feedback(null, thirdBooking, customer,
                        5.0, positiveComments[6], LocalDateTime.now().minusDays(28)));
            }
        }

        // Create payments for sample bookings
        if (paymentRepository.findAll().isEmpty()) {
            List<Booking> bookings = bookingRepository.findAll();

            if (!bookings.isEmpty()) {
                Booking booking1 = bookings.get(0);
                Booking booking2 = bookings.get(1);

                // Payment thành công qua VNPAY
                Payment payment1 = new Payment();
                payment1.setCode(paymentRepository.generateCode());
                payment1.setTransactionNo("VNPAY123456");
                payment1.setAmount(booking1.getRoom().getRoomType().getPricePerNight());
                payment1.setMethod(Payment.Method.VNPAY);
                payment1.setStatus(Payment.Status.SUCCESS);
                payment1.setDescription("Thanh toán phòng qua VNPAY");
                payment1.setBooking(booking1);
                paymentRepository.createOrUpdate(payment1);

                Payment payment2 = new Payment();
                payment2.setCode(paymentRepository.generateCode());
                payment2.setTransactionNo("TRANSFER987654");
                payment2.setAmount(booking2.getRoom().getRoomType().getPricePerNight());
                payment2.setMethod(Payment.Method.TRANSFER);
                payment2.setStatus(Payment.Status.PENDING);
                payment2.setDescription("Thanh toán phòng qua MOMO");
                payment2.setBooking(booking2);
                paymentRepository.createOrUpdate(payment2);
            }
        }

        if (invoiceRepository.findAll().isEmpty()) {
            List<Booking> bookings = bookingRepository.findAll();
            if (!bookings.isEmpty()) {
                Booking booking1 = bookings.get(0);
                Booking booking2 = bookings.get(1);

                // Invoice cho booking1 (đã có Payment thành công)
                Invoice invoice1 = new Invoice();
                invoice1.setBooking(booking1);
                invoice1.setIssueAt(LocalDateTime.now());
                invoice1.setInvoiceNumber("INV-" + System.currentTimeMillis());
                invoice1.setSentToEmail(booking1.getUser().getEmail());
                invoice1.setStatus(Invoice.Status.PAID);
                invoiceRepository.createOrUpdate(invoice1);

                // Invoice cho booking2 (Payment đang pending)
                Invoice invoice2 = new Invoice();
                invoice2.setBooking(booking2);
                invoice2.setIssueAt(LocalDateTime.now());
                invoice2.setInvoiceNumber("INV-" + (System.currentTimeMillis() + 1));
                invoice2.setSentToEmail(booking2.getUser().getEmail());
                invoice2.setStatus(Invoice.Status.UNPAID); // hoặc PARTIALLY_PAID nếu có một phần tiền
                invoiceRepository.createOrUpdate(invoice2);
            }
        }
    }
}
