package ian.hu.wechat.sdk.service.user.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import ian.hu.wechat.sdk.service.Errors;
import ian.hu.wechat.sdk.service.core.result.Result;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取所有分组结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetGroupsResult extends Result {
    private static final long serialVersionUID = 728913681992507229L;
    @JsonProperty("groups")
    private List<GroupItem> groups;

    @Override
    public Long getErrorCode() {
        return groups != null ? Errors.OK.getCode() : super.getErrorCode();
    }
}
