package cn.tianjiale;


import cn.tianjiale.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.tianjiale.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.tianjiale.trigger.api.dto.RaffleRequestDTO;
import cn.tianjiale.trigger.api.dto.RaffleResponseDTO;
import cn.tianjiale.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface IRaffleService {
    /**
     * 策略装配接口
     * @param strategyId
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO raffleAwardListRequestDTO);
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
