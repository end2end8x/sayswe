package beliveapp.io.chat.model;

import android.media.Image;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

/**
 * Created by phungkhactuan on 3/23/18.
 */

public class Message implements IMessage,
        MessageContentType.Image {

   /*...*/

    private String id;
    private String text;
    private Author author;
    private Date createdAt;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getImageUrl() {
        return null; // image == null ? null : image.url;
    }
}
