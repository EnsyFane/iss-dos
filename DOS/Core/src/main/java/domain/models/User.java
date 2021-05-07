package domain.models;

import utils.Constants;

import java.sql.Date;
import java.util.Objects;

public class User extends Entity<Integer> {
    private String userName;
    private String firstName;
    private String lastName;
    private String encryptedPassword;
    private String salt;
    private UserType userType;
    private String email;
    private Date nextPasswordChange;

    public User() {}

    public User(Integer id, String userName, String firstName, String lastName, String encryptedPassword, String salt, UserType userType, String email, Date nextPasswordChange) {
        super(id);
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.encryptedPassword = encryptedPassword;
        this.salt = salt;
        this.userType = userType;
        this.email = email;
        this.nextPasswordChange = nextPasswordChange;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getNextPasswordChange() {
        return nextPasswordChange;
    }

    public void setNextPasswordChange(Date nextPasswordChange) {
        this.nextPasswordChange = nextPasswordChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getUserName().equals(user.getUserName()) &&
                getFirstName().equals(user.getFirstName()) &&
                getLastName().equals(user.getLastName()) &&
                getEncryptedPassword().equals(user.getEncryptedPassword()) &&
                getSalt().equals(user.getSalt()) &&
                getUserType() == user.getUserType() &&
                getEmail().equals(user.getEmail()) &&
                getNextPasswordChange().equals(user.getNextPasswordChange());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getFirstName(), getLastName(), getEncryptedPassword(), getSalt(), getUserType(), getEmail(), getNextPasswordChange());
    }

    public static class Builder {
        private Integer _id;
        private String _userName;
        private String _firstName;
        private String _lastName;
        private String _encryptedPassword;
        private String _salt;
        private UserType _userType;
        private String _email;
        private Date _nextPasswordChange;

        public Builder() {
            _id = 0;
            _userName = "user-name";
            _firstName = "first-name";
            _lastName = "last-name";
            _encryptedPassword = "a".repeat(Constants.ENCRYPTED_PASSWORD_LENGTH);
            _salt = "a".repeat(Constants.SALT_LENGTH);
            _userType = UserType.Admin;
            _email = "email@dos.com";
            _nextPasswordChange = new Date(System.currentTimeMillis());
        }

        public Builder from(User other) {
            _id = other.getId();
            _userName = other.getUserName();
            _firstName = other.getFirstName();
            _lastName = other.getLastName();
            _encryptedPassword = other.getEncryptedPassword();
            _salt = other.getSalt();
            _userType = other.getUserType();
            _email = other.getEmail();
            _nextPasswordChange = other.getNextPasswordChange();
            return this;
        }

        public Builder withId(Integer id) {
            _id = id;
            return this;
        }

        public Builder withUserName(String userName) {
            _userName = userName;
            return this;
        }

        public Builder withFirstName(String firstName) {
            _firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            _lastName = lastName;
            return this;
        }

        public Builder withEncryptedPassword(String encryptedPassword) {
            _encryptedPassword = encryptedPassword;
            return this;
        }

        public Builder withSalt(String salt) {
            _salt = salt;
            return this;
        }

        public Builder withUserType(UserType userType) {
            _userType = userType;
            return this;
        }

        public Builder withEmail(String email) {
            _email = email;
            return this;
        }

        public Builder withNextPasswordChange(Date nextPasswordChange) {
            _nextPasswordChange = nextPasswordChange;
            return this;
        }

        public User build() {
            var user = new User();
            user.setId(_id);
            user.setUserName(_userName);
            user.setFirstName(_firstName);
            user.setLastName(_lastName);
            user.setEncryptedPassword(_encryptedPassword);
            user.setSalt(_salt);
            user.setUserType(_userType);
            user.setEmail(_email);
            user.setNextPasswordChange(_nextPasswordChange);

            return user;
        }
    }
}
