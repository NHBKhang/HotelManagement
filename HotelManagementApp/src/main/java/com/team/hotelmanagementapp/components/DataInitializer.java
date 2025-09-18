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
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.repositories.FeedbackRepository;
import com.team.hotelmanagementapp.repositories.InvoiceRepository;
import com.team.hotelmanagementapp.repositories.PaymentRepository;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import com.team.hotelmanagementapp.repositories.ServiceBookingRepository;
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
    private ServiceBookingRepository serviceBookingRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @PostConstruct
    public void init() {
        // Create users
        User admin = null, customer = null, customer1 = null;
        if (userService.getByUsername("admin") == null) {
            admin = new User("admin", "admin", "admin@gmail.com", User.Role.ADMIN, "Nguyễn", "Admin", "0123456789", User.Gender.MALE);
            customer = new User("customer", "customer", "customer@gmail.com", User.Role.CUSTOMER, "Trần Thị", "Na", "0967456615", User.Gender.FEMALE);

            userService.createOrUpdate(admin);
            userService.createOrUpdate(customer);

            userService.createOrUpdate(new User("accountant", "accountant", "accountant@gmail.com", User.Role.ACCOUNTANT, "Lê Thị", "Thơm", "0122336455", User.Gender.FEMALE));
            userService.createOrUpdate(new User("receiptionist", "receiptionist", "receiptionist@gmail.com", User.Role.RECEPTIONIST, "Phan Đình", "Dũng", "01763242544", User.Gender.MALE));
            userService.createOrUpdate(new User("housekeeper", "housekeeper", "housekeeper@gmail.com", User.Role.HOUSEKEEPING, "Johny", "Đặng", "0755434442777", User.Gender.MALE));
            userService.createOrUpdate(
                    new User("customer1", "customer1", "customer1@gmail.com", User.Role.CUSTOMER, "Trần", "Anh", "0911111111", User.Gender.MALE)
            );
            userService.createOrUpdate(
                    new User("customer2", "customer2", "customer2@gmail.com", User.Role.CUSTOMER, "Lê", "Bình", "0922222222", User.Gender.MALE)
            );
            userService.createOrUpdate(
                    new User("customer3", "customer3", "customer3@gmail.com", User.Role.CUSTOMER, "Phạm", "Cường", "0933333333", User.Gender.MALE)
            );
            userService.createOrUpdate(
                    new User("customer4", "customer4", "customer4@gmail.com", User.Role.CUSTOMER, "Ngô", "Dương", "0944444444", User.Gender.MALE)
            );
            userService.createOrUpdate(
                    new User("customer5", "customer5", "customer5@gmail.com", User.Role.CUSTOMER, "Hoàng", "Hưng", "0955555555", User.Gender.MALE)
            );
            userService.createOrUpdate(
                    new User("customer6", "customer6", "customer6@gmail.com", User.Role.CUSTOMER, "Dương", "Minh", "0966666666", User.Gender.MALE)
            );
        } else {
            admin = userService.getByUsername("admin");
            customer = userService.getByUsername("customer");
            customer1 = userService.getByUsername("customer2");
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
            Service s1 = serviceRepository.createOrUpdate(new Service(null, "Bữa sáng", "Bữa sáng đơn giản", 50000.0, true, "suất", true));
            Service s2 = serviceRepository.createOrUpdate(new Service(null, "Bữa trưa buffet", "Bữa trưa buffet", 150000.0, true, "suất", true));
            serviceRepository.createOrUpdate(new Service(null, "Bữa tối buffet", "Bữa tối buffet", 250000.0, true, "suất", true));
            serviceRepository.createOrUpdate(new Service(null, "Spa thư giãn", "Phiên spa thư giãn 1 giờ", 55000.0, true, "giờ", true));
            serviceRepository.createOrUpdate(new Service(null, "Phục vụ phòng", "Phục vụ đồ uống và ăn nhẹ tại phòng", 180000.0, true, "lượt", false));
            serviceRepository.createOrUpdate(new Service(null, "Giặt ủi", "Giặt ủi 1 bộ trang phục", 55000.0, true, "lượt", true));
            serviceRepository.createOrUpdate(new Service(null, "Gym & Fitness", "Vé tập gym 1 ngày", 85000.0, true, "ngày", true));
            serviceRepository.createOrUpdate(new Service(null, "Internet Wifi", "Internet tốc độ cao 1 ngày", 50000.0, true, "ngày", false));
            Service s9 = serviceRepository.createOrUpdate(new Service(null, "Chỗ đậu xe", "Đậu xe an toàn 1 ngày", 30000.0, true, "ngày", true));
            serviceRepository.createOrUpdate(new Service(null, "Dịch vụ đưa đón", "Đưa đón bằng xe riêng", 400000.0, true, "lượt", false));

            serviceBookingRepository.createOrUpdate(new ServiceBooking(null, bookingRepository.getById(1), s9, 2));
            serviceBookingRepository.createOrUpdate(new ServiceBooking(null, bookingRepository.getById(1), s1, 1));
            serviceBookingRepository.createOrUpdate(new ServiceBooking(null, bookingRepository.getById(2), s2, 1));
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
                    completedBooking.setStatus(Booking.Status.CONFIRMED); // Simulate completion
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
                Booking secondBooking = new Booking(null, customer1, anotherRoom,
                        LocalDate.now().minusDays(20), LocalDate.now().minusDays(18),
                        Booking.Status.CHECKED_OUT, null, 2);
                secondBooking = bookingRepository.createOrUpdate(secondBooking);

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer1,
                        4.0, serviceComments[1], LocalDateTime.now().minusDays(16)));

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer1,
                        3.0, constructiveComments[0], LocalDateTime.now().minusDays(15)));

                feedbackRepository.createOrUpdate(new Feedback(null, secondBooking, customer1,
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

        if (paymentRepository.findAll().isEmpty() && invoiceRepository.findAll().isEmpty()) {
            List<Booking> bookings = bookingRepository.findAll();

            if (!bookings.isEmpty()) {
                Booking booking1 = bookings.get(0);
                Booking booking2 = bookings.get(1);
                Booking booking3 = bookings.get(2);

                // ===== Invoice 1 cho booking1 =====
                Invoice invoice1 = new Invoice();
                invoice1.setBooking(booking1);
                invoice1.setIssueAt(LocalDateTime.now());
                invoice1.setInvoiceNumber("INV-" + System.currentTimeMillis());
                invoice1.setSentToEmail(booking1.getUser().getEmail());
                invoice1.setStatus(Invoice.Status.UNPAID);
                invoice1.setTotalAmount(booking1.getRoom().getRoomType().getPricePerNight() + 150000);
                invoice1 = invoiceRepository.createOrUpdate(invoice1);

                // Payment trả góp 2 lần cho invoice1
                Payment payment1a = new Payment();
                payment1a.setCode(paymentRepository.generateCode());
                payment1a.setTransactionNo("VNPAY123456");
                payment1a.setAmount(booking1.getRoom().getRoomType().getPricePerNight() / 2 + 100000);
                payment1a.setMethod(Payment.Method.VNPAY);
                payment1a.setStatus(Payment.Status.SUCCESS);
                payment1a.setDescription("Thanh toán đợt 1 qua VNPAY");
                payment1a.setInvoice(invoice1); // ✅ liên kết invoice
                paymentRepository.createOrUpdate(payment1a);

                Payment payment1b = new Payment();
                payment1b.setCode(paymentRepository.generateCode());
                payment1b.setTransactionNo("VNPAY654321");
                payment1b.setAmount(booking1.getRoom().getRoomType().getPricePerNight() / 2 + 50000);
                payment1b.setMethod(Payment.Method.VNPAY);
                payment1b.setStatus(Payment.Status.SUCCESS);
                payment1b.setDescription("Thanh toán đợt 2 qua VNPAY");
                payment1b.setInvoice(invoice1);
                paymentRepository.createOrUpdate(payment1b);

                invoice1.setStatus(Invoice.Status.PAID);
                invoiceRepository.createOrUpdate(invoice1);

                // ===== Invoice 2 cho booking2 =====
                Invoice invoice2 = new Invoice();
                invoice2.setBooking(booking2);
                invoice2.setIssueAt(LocalDateTime.now());
                invoice2.setInvoiceNumber("INV-" + (System.currentTimeMillis() + 1));
                invoice2.setSentToEmail(booking2.getUser().getEmail());
                invoice2.setStatus(Invoice.Status.UNPAID);
                invoice2.setTotalAmount(booking2.getRoom().getRoomType().getPricePerNight() + 50000);
                invoice2 = invoiceRepository.createOrUpdate(invoice2);

                // Payment mới pending
                Payment payment2 = new Payment();
                payment2.setCode(paymentRepository.generateCode());
                payment2.setTransactionNo("TRANSFER987654");
                payment2.setAmount(booking2.getRoom().getRoomType().getPricePerNight() + 50000);
                payment2.setMethod(Payment.Method.TRANSFER);
                payment2.setStatus(Payment.Status.PENDING);
                payment2.setDescription("Thanh toán qua chuyển khoản");
                payment2.setInvoice(invoice2); // ✅ gắn vào invoice2
                paymentRepository.createOrUpdate(payment2);

                invoice2.setStatus(Invoice.Status.UNPAID); // vì chưa thanh toán đủ
                invoiceRepository.createOrUpdate(invoice2);

                // Invoice 3
                Invoice pastInvoice = new Invoice();
                pastInvoice.setBooking(booking3);
                pastInvoice.setIssueAt(LocalDateTime.now().minusMonths(1));
                pastInvoice.setInvoiceNumber("INV-" + (System.currentTimeMillis() - 10000));
                pastInvoice.setSentToEmail(booking3.getUser().getEmail());
                pastInvoice.setStatus(Invoice.Status.PAID);

                double amount = booking3.getRoom().getRoomType().getPricePerNight();
                pastInvoice.setTotalAmount(amount * 2);

                pastInvoice = invoiceRepository.createOrUpdate(pastInvoice);

                Payment pastPayment = new Payment();
                pastPayment.setCode(paymentRepository.generateCode());
                pastPayment.setTransactionNo("TRANSFER987654");
                pastPayment.setAmount(amount);
                pastPayment.setMethod(Payment.Method.TRANSFER);
                pastPayment.setStatus(Payment.Status.SUCCESS);
                pastPayment.setDescription("Thanh toán tháng 8");
                pastPayment.setInvoice(pastInvoice);

                paymentRepository.createOrUpdate(pastPayment);
            }
        }
    }
}
