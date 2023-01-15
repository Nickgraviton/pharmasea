package priceobservatory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.dto.PriceDTO;
import priceobservatory.dto.ProductDTO;
import priceobservatory.dto.ShopDTO;
import priceobservatory.dto.UserDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.model.Token;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {
    TokenService tokenService;

    ValidationService(@Autowired TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public boolean isValidStart(Integer start) {
        return start >= 0;
    }

    public boolean isValidCount(Integer count) {
        return count > 0;
    }

    public boolean isValidFormat(String format) {
        return format.equals("json");
    }

    public boolean isValidDistance(Integer geoDist, Double geoLng, Double geoLat) {
        return (geoDist != null && geoLng != null && geoLat != null)
                || (geoDist == null && geoLng == null && geoLat == null);
    }

    public boolean isValidDateInterval(Date dateFrom, Date dateTo) {
        return (dateFrom != null && dateTo != null)
                || (dateFrom == null && dateTo == null);
    }

    public boolean isValidSortList(List<String> sort) {
        // It's only possible to sort by geoDist, price and date.
        // If a sorting type reappears then the size will exceed 3
        boolean validSort = (sort.size() <= 3);

        String[] acceptableSort = {"geoDist|ASC", "geoDist|DESC", "price|ASC", "price|DESC", "date|ASC", "date|DESC"};
        for (String s : sort) {
            if (!Arrays.asList(acceptableSort).contains(s))
                validSort = false;
        }
        return validSort;
    }

    public boolean isValidToken(String token) {
        if (token == null)
            return false;

        Optional<Token> t = tokenService.findByToken(token);
        return t.isPresent();
    }

    public void validateToken(String token) {
        if (!isValidToken(token))
            throw new BadRequestException("Invalid token");
    }

    public String determineRole(String token) {
        Optional<Token> t = tokenService.findByToken(token);
        if (t.isPresent()) {
            return t.get().getRole();
        } else {
            throw new BadRequestException("Invalid token");
        }
    }
    public void checkAdminRole(String token) {
        String role = determineRole(token);
        if (!role.equals("admin"))
            throw new UnauthorizedException("Error: Only admins can perform this action");
    }

    public void validateGetParameters(String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
    }

    public void validateGetParameters(Integer start, Integer count, String format) {
        boolean validStart = isValidStart(start);
        boolean validCount = isValidCount(count);
        boolean validFormat = isValidFormat(format);

        if (!validStart || !validCount || !validFormat) {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    public void validateGetParameters(Integer start, Integer count, String status, String format) {
        boolean validStart = isValidStart(start);
        boolean validCount = isValidCount(count);
        boolean validFormat = isValidFormat(format);
        boolean validStatus;

        String[] acceptableStatus = { "ALL", "ACTIVE", "WITHDRAWN" };
        validStatus = Arrays.asList(acceptableStatus).contains(status);

        if (!validStart || !validCount || !validStatus || !validFormat) {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validStatus)
                builder.append("Invalid status parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    public void validateGetParameters(Integer start, Integer count, String status, String sort, String format) {
        boolean validStart = isValidStart(start);
        boolean validCount = isValidCount(count);
        boolean validFormat = isValidFormat(format);
        boolean validStatus, validSort;

        String[] acceptableStatus = { "ALL", "ACTIVE", "WITHDRAWN" };
        validStatus = Arrays.asList(acceptableStatus).contains(status);

        String[] acceptableSort = { "id|ASC", "id|DESC", "name|ASC", "name|DESC" };
        validSort = Arrays.asList(acceptableSort).contains(sort);

        if (!validStart || !validCount || !validStatus || !validSort || !validFormat) {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validStatus)
                builder.append("Invalid status parameter, ");
            if (!validSort)
                builder.append("Invalid sort parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    public void validateGetParameters(
            Integer start,
            Integer count,
            String format,
            Integer geoDist,
            Double geoLng,
            Double geoLat,
            Date dateFrom,
            Date dateTo,
            List<String> sort
    ) {
        boolean validStart = isValidStart(start);
        boolean validCount = isValidCount(count);
        boolean validSortList = isValidSortList(sort);
        boolean validDist = isValidDistance(geoDist, geoLng, geoLat);
        boolean validDate = isValidDateInterval(dateFrom, dateTo);
        boolean validFormat = isValidFormat(format);

        if (!validStart || !validCount || !validSortList || !validDist || !validDate || !validFormat) {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validSortList)
                builder.append("Invalid sort parameter, ");
            if (!validDist)
                builder.append("Invalid distance parameters, ");
            if (!validDate)
                builder.append("Invalid date parameters, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    public void validateDeleteParameters(String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
    }

    public void validatePostParameters(UserDTO newUser, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");

        if (newUser.anyNull())
            throw new BadRequestException("Error: Invalid user field(s)");
    }

    public void validatePostParameters(PriceDTO newPrice, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
        if (newPrice.anyNull())
            throw new BadRequestException("Invalid price field(s)");
    }

    public void validatePostParameters(ProductDTO newProduct, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
        if (newProduct.anyNull())
            throw new BadRequestException("Invalid price field(s)");
    }

    public void validatePostParameters(ShopDTO newShop, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
        if (newShop.anyNull())
            throw new BadRequestException("Invalid price field(s)");
    }

    public void validatePutParameters(ProductDTO newProduct, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
        if (newProduct.anyNull())
            throw new BadRequestException("Invalid price field(s)");
    }

    public void validatePutParameters(ShopDTO newShop, String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
        if (newShop.anyNull())
            throw new BadRequestException("Invalid price field(s)");
    }

    public void validatePatchParameters(String format) {
        if (!isValidFormat(format))
            throw new BadRequestException("Error: Invalid format");
    }
}
