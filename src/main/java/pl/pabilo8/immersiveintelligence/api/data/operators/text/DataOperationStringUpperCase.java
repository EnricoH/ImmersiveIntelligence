package pl.pabilo8.immersiveintelligence.api.data.operators.text;

import pl.pabilo8.immersiveintelligence.api.data.DataPacket;
import pl.pabilo8.immersiveintelligence.api.data.operators.DataOperator;
import pl.pabilo8.immersiveintelligence.api.data.types.DataPacketTypeExpression;
import pl.pabilo8.immersiveintelligence.api.data.types.DataPacketTypeNull;
import pl.pabilo8.immersiveintelligence.api.data.types.DataPacketTypeString;
import pl.pabilo8.immersiveintelligence.api.data.types.IDataType;

/**
 * @author Pabilo8
 * @since 05-07-2019
 */
public class DataOperationStringUpperCase extends DataOperator
{
	public DataOperationStringUpperCase()
	{
		//how many characters a string contains
		name = "string_uppercase";
		sign = "";
		allowedType1 = DataPacketTypeString.class;
		allowedType2 = DataPacketTypeNull.class;
		expectedResult = DataPacketTypeString.class;
	}

	@Override
	public IDataType execute(DataPacket packet, DataPacketTypeExpression data)
	{
		IDataType t1 = getVarInType(DataPacketTypeString.class, data.getType1(), packet);
		return new DataPacketTypeString(t1.valueToString().toUpperCase());
	}
}
