package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.entity.User;

public interface UserPasswordService {
    void setupMemberInitialPassword(User user);
}
