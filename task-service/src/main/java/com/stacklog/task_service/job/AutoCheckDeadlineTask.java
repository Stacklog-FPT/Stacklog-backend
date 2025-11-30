package com.stacklog.task_service.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.repo.TaskRepo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoCheckDeadlineTask {

  @Autowired
  private TaskRepo taskRepo;

  @Autowired
  private KafkaProducer<TaskMessageQueue> kafkaTaskProducer;

  @Autowired
  private KafkaProducer<MessageEmailKafka> kafkaMessKafkaProducer;

  private final String TOPIC_BY_DATE = "task-service.task.deadline-by-date";

  @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
  public void checkDeadlineDate() {
    log.info("🔎 Running AutoCheckDeadlineTask...");

    LocalDateTime now = LocalDateTime.now();

    // Lấy tất cả tasks còn hạn (không Completed)
    List<Task> tasks = taskRepo.findAll();

    for (Task task : tasks) {
      if (task.getTaskDueDate() == null)
        continue;

      boolean isCompleted = task.getStatusTask() != null &&
          "Completed".equalsIgnoreCase(task.getStatusTask().getStatusTaskName());

      if (isCompleted)
        continue;

      // Điều kiện "sắp đến hạn" — tùy bạn chỉnh
      long minutesUntilDue = Duration.between(now, task.getTaskDueDate()).toMinutes();

      // Ví dụ: gửi notification khi còn <= 60 phút
      if (minutesUntilDue <= 1440 && minutesUntilDue > 0) {

        TaskMessageQueue payload = new TaskMessageQueue(
            task.getGroupId(),
            task.getTaskId(),
            task.getTaskTitle(),
            task.getTaskDueDate(),
            task.getAssigns().stream()
                .map(a -> a.getAssignTo())
                .toList());

        kafkaTaskProducer.sendMessage(payload, TOPIC_BY_DATE);

        log.info("📤 Sent DEADLINE notification to Kafka → task {}", task.getTaskId());
      }
    }

    log.info("✅ AutoCheckDeadlineTask done.");
  }

  @Scheduled(fixedRate =  60 * 60 * 1000)
  public void checkDeadlineHour() {
    log.info("🔎 Running AutoCheckDeadlineTask...");

    LocalDateTime now = LocalDateTime.now();

    // Lấy tất cả tasks còn hạn (không Completed)
    List<Task> tasks = taskRepo.findAll();

    for (Task task : tasks) {
      if (task.getTaskDueDate() == null)
        continue;

      boolean isCompleted = task.getStatusTask() != null &&
          "Completed".equalsIgnoreCase(task.getStatusTask().getStatusTaskName());

      if (isCompleted)
        continue;

      // Điều kiện "sắp đến hạn" — tùy bạn chỉnh
      long minutesUntilDue = Duration.between(now, task.getTaskDueDate()).toMinutes();

      // Ví dụ: gửi notification khi còn <= 60 phút
      if (minutesUntilDue <= 60 && minutesUntilDue > 0) {

        MessageEmailKafka payload = new MessageEmailKafka();
        payload.setSubject("DEADLINE IS COMMING");
        payload.setContent("Deadline is behind you. you need to rush this task:" + task.getTaskTitle() + " click to:  " + task.getGroupId());
        // laáy email của những user được assign tới task đó gặp vấn đề là batch không có token :)))

        kafkaMessKafkaProducer.sendMessage(payload, "notification-service.email.send");

        log.info("📤 Sent DEADLINE notification to Kafka → task {}", task.getTaskId());
      }
    }

    log.info("✅ AutoCheckDeadlineTask done.");
  }

}

@Getter
@Setter
@AllArgsConstructor
class TaskMessageQueue {
  private String groupId;
  private String taskId;
  private String taskTitle;
  private LocalDateTime taskDueDate;
  private List<String> assigns;
}

@Getter
@Setter
class MessageEmailKafka {
  private String subject;
  private String content;
  private List<String> receivers;
}