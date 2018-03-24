package beliveapp.io.chat.model;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by phungkhactuan on 3/23/18.
 */

public class Author implements IUser {

   /*...*/

    private String id;
    private String name;
    private String avatar;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}