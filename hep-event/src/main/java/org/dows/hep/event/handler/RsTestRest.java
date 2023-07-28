package org.dows.hep.event.handler;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.exception.ExperimentInitHanlderException;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class RsTestRest {
  private final ExperimentInitHandler experimentInitHandler;

  @Operation(summary = "手动掉实验接口")
  @PostMapping("v1/experimentIndicator/test")
  public void testExp(@RequestBody ExperimentGroupSettingRequest request) throws ExecutionException, InterruptedException {
    experimentInitHandler.exec(request);
  }
}
