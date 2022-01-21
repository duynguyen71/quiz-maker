package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Option;
import com.nkd.quizmaker.repo.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepo;

    public Option getById(Long id){
        Optional<Option> optional = optionRepo.findById(id);
        if(optional.isPresent())
            return optional.get();
        return null;
    }
    public Option save(Option option
    ){
        return optionRepo.save(option);
    }
}
