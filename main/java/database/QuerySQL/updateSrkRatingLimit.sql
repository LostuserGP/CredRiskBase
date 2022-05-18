DECLARE @ID INT;
DECLARE @DepLimit INT;
DECLARE @DenLimit INT;
DECLARE @DtoLimit INT;
SET @ID = ?;
SET @DepLimit = ?;
SET @DenLimit = ?;
SET @DtoLimit = ?;

UPDATE dbo.ctl_RatingValues
SET DepLimit = @DepLimit,
DenLimit = @DenLimit,
DtoLimit = @DtoLimit
WHERE ID = @ID