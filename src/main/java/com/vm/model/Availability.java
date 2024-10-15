package com.vm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "availabilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // userId dưới dạng String
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Ngày trong tuần (1 = Thứ Hai, ..., 7 = Chủ Nhật)
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // Số thứ tự của ca trong ngày (ví dụ: 1 cho ca sáng, 2 cho ca chiều)
    @Column(name = "shift_number", nullable = false)
    private Integer shiftNumber;

    // Thời gian bắt đầu ca làm việc
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Thời gian kết thúc ca làm việc
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
