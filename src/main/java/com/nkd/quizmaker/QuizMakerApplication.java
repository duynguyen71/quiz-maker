package com.nkd.quizmaker;

import com.nkd.quizmaker.enumm.ERole;
import com.nkd.quizmaker.service.UserServiceHelper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties({FileStorageProperties.class})
@Slf4j
public class QuizMakerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(QuizMakerApplication.class, args);
    }

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserServiceHelper userService;

    @Override
    public void run(String... args) throws Exception {
//        saveusers();
//        Role roleMember = new Role();
//        roleMember.setName(ERole.ROLE_MEMBER.name());
//        roleRepository.save(roleMember);
//        Role roleAdmin = new Role();
//        roleAdmin.setName(ERole.ROLE_ADMIN.name());
//        roleRepository.save(roleAdmin);
//        saveusers();

//        Quiz springBootQuiz = new Quiz();
//        springBootQuiz.setTitle("Spring Boot Quiz");
//        //question 1
//        Question q1 = new Question();
//        q1.setTitle("Spring is a");
//        //question 1 - option 1
//        Option q1o1 = new Option();
//        q1o1.setContent("Web Development Framework");
//        q1o1.setScore(1.0);
//        //question 1 - option 2
//        Option q1o2 = new Option();
//        q1o2.setContent("Java Framework");
//        q1o2.setScore(1.0);
//        //question 1 - option 3
//        Option q1o3 = new Option();
//        q1o3.setContent("MVC Framework");
//        q1o3.setScore(1.0);
//        //question 1 - option 4
//        Option q1o4 = new Option();
//        q1o4.setContent("None of the Above");
//        q1o4.setScore(0.0);
//
//        q1.setOptions(Arrays.asList(q1o1, q1o2, q1o3, q1o4));
//
//
//        //question 2
//        Question q2 = new Question();
//        q2.setTitle("What is default HTML template engine in spring boot");
//        //question 2 - option 1
//        Option q2o1 = new Option();
//        q2o1.setContent("JSP");
//        q2o1.setScore(0.0);
//        //question 2 - option 2
//        Option q2o2 = new Option();
//        q2o2.setContent("HTML");
//        q2o2.setScore(0.0);
//        //question 2 - option 3
//        Option q2o3 = new Option();
//        q2o3.setContent("Thymeleaf");
//        q2o3.setScore(1.0);
//        //question 2 - option 4
//        Option q2o4 = new Option();
//        q2o4.setContent("None of the Above");
//        q2o4.setScore(0.0);
//
//        q2.setOptions(Arrays.asList(q2o1, q2o2, q2o3, q2o4));
//
//        springBootQuiz.setQuestions(Arrays.asList(q1, q2));
//
////        quizRepository.save(springBootQuiz);
//
//        quizServiceHelper.saveQuiz(springBootQuiz);

//        QuizDto springBootQuiz = new QuizDto();
//        springBootQuiz.setTitle("Spring Boot Quiz");
//        //question 1
//        QuestionDto q1 = new QuestionDto();
//        q1.setTitle("Spring is a");
//        q1.setQuiz(springBootQuiz);
//        //question 1 - option 1
//        OptionDto q1o1 = new OptionDto();
//        q1o1.setContent("Web Development Framework");
//        q1o1.setScore(1.0);
//        q1o1.setQuestion(q1);
//        //question 1 - option 2
//        OptionDto q1o2 = new OptionDto();
//        q1o2.setContent("Java Framework");
//        q1o2.setScore(1.0);
//        q1o2.setQuestion(q1);
//        //question 1 - option 3
//        OptionDto q1o3 = new OptionDto();
//        q1o3.setContent("MVC Framework");
//        q1o3.setScore(1.0);
//        q1o3.setQuestion(q1);
//        //question 1 - option 4
//        OptionDto q1o4 = new OptionDto();
//        q1o4.setContent("None of the Above");
//        q1o4.setScore(0.0);
//        q1o4.setQuestion(q1);
//
//        q1.setOptions(Arrays.asList(q1o1, q1o2, q1o3, q1o4));
//
//
//        //question 2
//        QuestionDto q2 = new QuestionDto();
//        q2.setTitle("What is default HTML template engine in spring boot");
//        q2.setQuiz(springBootQuiz);
//        //question 2 - option 1
//        OptionDto q2o1 = new OptionDto();
//        q2o1.setContent("JSP");
//        q2o1.setScore(0.0);
//        q2o1.setQuestion(q2);
//        //question 2 - option 2
//        OptionDto q2o2 = new OptionDto();
//        q2o2.setContent("HTML");
//        q2o2.setScore(0.0);
//        q2o2.setQuestion(q2);
//        //question 2 - option 3
//        OptionDto q2o3 = new OptionDto();
//        q2o3.setContent("Thymeleaf");
//        q2o3.setScore(1.0);
//        q2o3.setQuestion(q2);
//        //question 2 - option 4
//        OptionDto q2o4 = new OptionDto();
//        q2o4.setContent("None of the Above");
//        q2o4.setScore(0.0);
//        q2o4.setQuestion(q2);
//
//        q2.setOptions(Arrays.asList(q2o1, q2o2, q2o3, q2o4));
//
//        springBootQuiz.setQuestions(Arrays.asList(q1, q2));
//
//
//        QuizMapper.toQuiz(springBootQuiz);
//        quizRepository.save(springBootQuiz)

    }

    private void saveusers() {
        Role roleMember = new Role();
        roleMember.setName(ERole.ROLE_MEMBER.name());
        roleRepository.save(roleMember);
        Role roleAdmin = new Role();
        roleAdmin.setName(ERole.ROLE_ADMIN.name());
        roleRepository.save(roleAdmin);

        List<Role> roles = new ArrayList<>();
        roles.add(roleMember);

        User test = new User();
        test.setUsername("test");
        test.setPassword("password");
        test.setEmail("test@gmail.com");
        test.setRoles(roles);

//        test.setAddress(a1);
        test.setAvtImgUrl("https://images.pexels.com/photos/1081685/pexels-photo-1081685.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
//        userService.saveFull(test);
userRepository.save(test);
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEmail("admin@gmail.com");
//        admin.setAddress(a2);
        admin.setAvtImgUrl("https://images.pexels.com/photos/1559486/pexels-photo-1559486.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        admin.setRoles(Collections.singletonList(roleAdmin));
        userRepository.save(admin);
//
        User mia = new User();
        mia.setUsername("mia");
        mia.setPassword("password");
//        mia.setAddress(a1);
        mia.setEmail("mia@gmail.com");
        mia.setRoles(roles);
        mia.setAvtImgUrl("https://images.pexels.com/photos/1382731/pexels-photo-1382731.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        userRepository.save(mia);


        User member = new User();
        member.setUsername("member");
        member.setPassword("password");
        member.setEmail("member@gmail.com");
        member.setRoles(Collections.singletonList(roleMember));
        member.setAvtImgUrl("https://images.pexels.com/photos/8548511/pexels-photo-8548511.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500");
        userRepository.save(member);
    }
}
